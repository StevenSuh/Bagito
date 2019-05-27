from flask import Flask, jsonify, request
from datetime import datetime, timedelta
from db import app, db
from db.models import User, Bag, Rental, Bin, Returned, Plan
from sqlalchemy.exc import IntegrityError

import config
import defs
import hashlib
import random
import services.gsheets
import services.mailjet
import services.stripe

# input: email, password
# output: msg (only if error)
@app.route('/api/login', methods=['POST'])
def login():
  email = request.form.get('email', None)
  password = request.form.get('password', None)

  if any(v is None for v in [email, password]):
    return jsonify(msg='Invalid parameters'), 400

  email = email.lower()

  user = User.query.filter_by(email=email).first()
  if user is None:
    return jsonify(msg='Account does not exist'), 404

  password_hash = hashlib.sha256((password + config.PASSWORD_SALT).encode('utf-8')).hexdigest()
  if user.password != password_hash:
    return jsonify(msg='Incorrect password'), 400

  return jsonify(
    user_id=user.id,
    user_name=user.name,
    user_email=user.email,
    user_city=user.city,
    user_state=user.state,
    user_has_payment=bool(user.stripe_payment_token)
  )

# input: name, email, password, city, state
# output: msg (only if error)
@app.route('/api/register', methods=['POST'])
def register():
  name = request.form.get('name', None)
  email = request.form.get('email', None)
  password = request.form.get('password', None)
  city = request.form.get('city', None)
  state = request.form.get('state', None)

  if any(v is None for v in [name, email, password, city, state]):
    return jsonify(msg='Invalid parameters'), 400

  email = email.lower()

  if state not in defs.STATE_LIST:
    return jsonify(msg='Invalid state'), 404

  password_hash = hashlib.sha256((password + config.PASSWORD_SALT).encode('utf-8')).hexdigest()

  new_user = User(name=name, email=email, password=password_hash, city=city, state=state)
  try:
    db.session.add(new_user)
    db.session.commit()
  except IntegrityError:
    db.session.rollback()
    return jsonify(msg='Account already exists'), 400

  customer = services.stripe.createCustomer(email, name)
  new_user.stripe_customer_token = customer.id
  db.session.commit()

  return jsonify()

@app.route('/api/forgot', methods=['POST'])
def forgot():
  email = request.form.get('email', None)

  if any(v is None for v in [email]):
    return jsonify(msg='Invalid parameters'), 400

  email = email.lower()

  user = User.query.filter_by(email=email).first()
  if user is None:
    return jsonify(msg='Account does not exist'), 404

  code = ''
  for i in range(4):
    code += str(random.randint(0, 9))

  user.code = code
  user.code_exp = datetime.now()

  db.session.commit()

  status = services.mailjet.send_email(user.email, user.name, user.code)
  if status != 200:
    return jsonify(msg='Email failed to send'), 500

  return jsonify()

@app.route('/api/forgot/code', methods=['POST'])
def forgot_code():
  code = request.form.get('code', None)
  email = request.form.get('email', None)

  if any(v is None for v in [code, email]):
    return jsonify(msg='Invalid parameters'), 400

  email = email.lower()

  user = User.query.filter_by(email=email, code=code).first()
  if user is None:
    return jsonify(msg='Invalid code'), 400

  curr_time_adjusted = datetime.now() - timedelta(hours=1)
  if curr_time_adjusted > user.code_exp:
    return jsonify(msg='Code expired'), 400

  return jsonify()

@app.route('/api/forgot/reset', methods=['POST'])
def forgot_reset():
  email = request.form.get('email', None)
  password = request.form.get('password', None)

  if any(v is None for v in [email, password]):
    return jsonify(msg='Invalid parameters'), 400

  email = email.lower()

  user = User.query.filter_by(email=email).first()
  if user is None:
    return jsonify(msg='Account does not exist'), 404

  password_hash = hashlib.sha256((password + config.PASSWORD_SALT).encode('utf-8')).hexdigest()

  user.password = password_hash
  user.code = None
  user.code_exp = None

  db.session.commit()

  return jsonify()

# input: query, index
# output: data
@app.route('/api/partner', methods=['GET'])
def partner():
  query = request.args.get('query', None)

  values = services.gsheets.get_partner_list(query)
  return jsonify(data=values)

# input: any of [Name, email, password, address, payment token]
# output: msg (only if error)
@app.route('/api/user/<user_id>/update', methods=['POST'])
def update(user_id):
  name = request.form.get('name', None)
  email = request.form.get('email', None)
  password = request.form.get('password', None)
  city = request.form.get('city', None)
  state = request.form.get('state', None)
  stripe_payment_token = request.form.get('stripe_payment_token', None)

  user = User.query.filter_by(id=user_id).first()
  if user is None:
    return jsonify(msg='Account does not exist'), 404

  if name:
    user.name = name
  if email:
    user.email = email.lower()
  if password:
    user.password = password
  if city:
    user.city = city
  if state:
    user.state = state
  if stripe_payment_token:
    customer_id = user.stripe_customer_token

    if user.stripe_payment_token is not None:
      response = services.stripe.deletePaymentMethod(user.stripe_payment_token, customer_id)
      if response is None:
        return jsonify(msg='Server error removing old payment method'), 500

    response = services.stripe.attachPaymentMethod(stripe_payment_token, customer_id)
    if response is None:
      return jsonify(msg='Server error adding payment method'), 500

    user.stripe_payment_token = stripe_payment_token

    if user.stripe_subscription_token is None or not user.has_paid:
      plan_id = Plan.query.all()[0].stripe_plan_token

      response = services.stripe.createSubscription(customer_id, plan_id)
      if response is None or response.latest_invoice is None:
        return jsonify(msg='Server error creating subscription'), 500

      user.stripe_subscription_token = response.id
      user.has_paid = 1

  db.session.commit()
  return jsonify()


@app.route('/api/user/<user_id>/cancel', methods=['GET'])
def cancel(user_id):
  user = User.query.filter_by(id=user_id).first()
  if user is None:
    return jsonify(msg='Account does not exist'), 404

  user.has_paid = 0

  response = services.stripe.cancelSubscription(user.stripe_subscription_token)
  if response is None or response.status != 'canceled':
    return jsonify(msg='Server failed to cancel subscription'), 500
  user.stripe_subscription_token = None

  response = services.stripe.deletePaymentMethod(user.stripe_payment_token, user.stripe_customer_token)
  if response is None:
    return jsonify(msg='Server failed to remove payment method'), 500
  user.stripe_payment_token = None

  db.session.commit()
  return jsonify()


# Gets the rental status
@app.route('/api/user/<user_id>/rent_status', methods=['GET'])
def rent_status(user_id):
  user = User.query.filter_by(id=user_id).first()
  if user is None:
    return jsonify(msg='Account does not exist'), 404

  bags = Bag.query.filter_by(current_user=user_id).all()

  values = []
  for bag in bags:
    rental = Rental.query.filter_by(bag_id=bag.id, returned_id=None).first()
    if rental is None:
      continue
    values.append({"date": rental.rental_date, "location": rental.location})

  return jsonify(values=values)


@app.route('/api/return/confirm', methods=['POST'])
def return_confirm():
  bag_qrcode_id = request.form.get('bag_qrcode_id', None)

  if any(v is None for v in [bag_qrcode_id]):
    return jsonify(msg='Invalid parameters'), 400

  current_bag = Bag.query.filter_by(qrcode_id=bag_qrcode_id).first()
  if current_bag is None:
    return jsonify(msg='Invalid bag'), 404

  return jsonify()


# Return a bag
@app.route('/api/return', methods=['POST'])
def return_bag():
  user_id = request.form.get('user_id', None)
  bag_qrcode_id = request.form.get('bag_qrcode_id', None)
  bin_qrcode_id = request.form.get('bin_qrcode_id', None)
  location = request.form.get('location', None)

  if any(v is None for v in [user_id, bag_qrcode_id, bin_qrcode_id]):
    return jsonify(msg='Invalid parameters'), 400

  user = User.query.filter_by(id=user_id).first()
  if user is None:
    return jsonify(msg='Account does not exist'), 404

  if not user.has_paid:
    return jsonify(msg='Account does not have a valid subscription'), 400

  current_bag = Bag.query.filter_by(qrcode_id=bag_qrcode_id).first()
  if current_bag is None:
    return jsonify(msg='Invalid bag'), 404
  if current_bag.current_user != user_id:
    return jsonify(msg='You have not rented this bag'), 400

  rental = Rental.query.filter_by(id=current_bag.rental_id).first()
  if rental is None:
    return jsonify(msg='Invalid rental'), 404

  current_bin = Bin.query.filter_by(qrcode_id=bin_qrcode_id).first()
  if current_bin is None:
    return jsonify(msg='Invalid bin'), 404

  current_bag.current_user = None
  current_bag.rental_id = None
  current_bag.bin_id = current_bin.id

  new_returned = Returned(
      location=location,
      bag_id=current_bag.id,
      return_date=datetime.now(),
      bin_id=current_bin.id,
      user_id=user_id
  )

  rental.returned_id = new_returned.id

  db.session.add(new_returned)
  db.session.commit()

  return jsonify()


@app.route('/api/rent', methods=['POST'])
def rent():
  user_id = request.form.get('user_id', None)
  bag_qrcode_id = request.form.get('bag_qrcode_id', None)
  location = request.form.get('location', None)

  if any(v is None for v in [user_id, bag_qrcode_id]):
    return jsonify(msg='Invalid parameters'), 400

  user = User.query.filter_by(id=user_id).first()
  if user is None:
    return jsonify(msg='Account does not exist'), 404

  if not user.has_paid:
    return jsonify(msg='Account does not have a valid subscription'), 400

  current_bag = Bag.query.filter_by(qrcode_id=bag_qrcode_id).first()
  if current_bag is None:
    return jsonify(msg='Invalid bag'), 404
  if current_bag.current_user:
    return jsonify(msg='Someone has already rented this bag'), 400

  current_bag.current_user = user_id
  current_bag.bin_id = None

  new_rental = Rental(
      location=location,
      rental_date=datetime.now(),
      bag_id=current_bag.id,
      user_id=user_id,
  )

  current_bag.rental_id = new_rental.id

  db.session.add(new_rental)
  db.session.commit()

  return jsonify()

if __name__ == '__main__':
  app.run(debug=True, use_reloader=True)

