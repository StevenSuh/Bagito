from flask import Flask, jsonify, request
from datetime import datetime, timedelta
from db import app, db
from db.models import User, Bag, Rental
from sqlalchemy.exc import IntegrityError

import config
import defs
import hashlib
import random
import services.gsheets
<<<<<<< HEAD
import datetime

# temporary index route
@app.route('/')
def index():
  return config.PASSWORD_SALT
||||||| merged common ancestors

# temporary index route
@app.route('/')
def index():
  return config.PASSWORD_SALT
=======
import services.mailjet
>>>>>>> 10888e314c177e22add2cafaaed04104971f6ce7

# input: email, password
# output: msg (only if error)
@app.route('/api/login', methods=['POST'])
def login():
  email = request.form['email'].lower()
  password = request.form['password']
  user = User.query.filter_by(email=email).first()
  if user is None:
    return jsonify(msg='Account does not exist'), 404

  password_hash = hashlib.sha256((password + config.PASSWORD_SALT).encode('utf-8')).hexdigest()
  if user.password != password_hash:
    return jsonify(msg='Incorrect password'), 400

  return jsonify()

# input: name, email, password, city, state
# output: msg (only if error)
@app.route('/api/register', methods=['POST'])
def register():
  name = request.form['name']
  email = request.form['email'].lower()
  password = request.form['password']
  city = request.form['city']
  state = request.form['state']

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

  return jsonify()

@app.route('/api/forgot', methods=['POST'])
def forgot():
  email = request.form['email'].lower()

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
  code = request.form['code']
  email = request.form['email']

  user = User.query.filter_by(email=email, code=code).first()
  if user is None:
    return jsonify(msg='Invalid code'), 400

  curr_time_adjusted = datetime.now() - timedelta(hours=1)
  if curr_time_adjusted > user.code_exp:
    return jsonify(msg='Code expired'), 400

  return jsonify()

@app.route('/api/forgot/reset', methods=['POST'])
def forgot_reset():
  email = request.form['email']
  password = request.form['password']

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
  index = request.args.get('index', 0)

  values = services.gsheets.get_partner_list(query, index)
  return jsonify(data=values)

# input: any of [Name, email, password, address, payment token]
# output: msg (only if error)
@app.route('/api/user/<user_id>/update', methods=['POST'])
def update(user_id):
  user = User.query.filter_by(id = user_id).first()
  name = request.form['name']
  email = request.form['email'].lower()
  password = request.form['password']
  address = request.form['address']
  payment_token= request.form['stripe_token']

  if name:
    user.name = name
  if email:
<<<<<<< HEAD
    user.email = email
  if password: 
    user.password = password
||||||| merged common ancestors
    user.email = email;
  if password: 
    user.password = password;
=======
    user.email = email;
  if password:
    user.password = password;
>>>>>>> 10888e314c177e22add2cafaaed04104971f6ce7
  if address:
    user.address = address
  if payment_token:
    user.payment_token = payment_token

  db.session.commit()

  return jsonify()

# Gets the rental status
@app.route('/api/user/<user_id>/rent_status', methods=['GET'])
def rentstatus(user_id):
  bags = Bag.query.filter_by(current_user=user_id).all()
  values = []
  for bag in bags:
    bag_id = bag.id
    rental = Rental.query.filter_by(bag_id=bag_id)
    values.append({"Date": rental.rental_date, "Location": rental.location})
  return jsonify(values=values)

# Return a bag

@app.route('/api/return',methods = ['POST'])
def return_bag():
  user_id = request.form['user_id']
  bag_id = request.form['bag_id']
  bin_id = request.form['bin_id']
  current_date = datetime.now()
  current_bag = Bag.query.filter_by(id=bag_id)
  current_bag.current_user=None
  current_bag.bin_id=bin_id
  new_returned = Returned(bag_id=bag_id,return_date=current_date,bin_id=bin_id,user_id=user_id)
  try:
    db.session.add(new_returned)
    db.session.commit()
  except IntegrityError:
    db.session.rollback()
<<<<<<< HEAD
  
<<<<<<< HEAD
@app.rout('/api/user/rent', methods=['POST'])
def rent:
  user_id = request.form['user_id']
  bag_id = request.form['bag_id']
  location_id = request.form['location']

  current_bag = Bag.query.filter_by(id=bag_id)
  
  rental_date = Rental.query.filter_by()
  
  datetime.datetime.now()

  bags = Bag.query.filter_by(current_user=user_id).all()

  
=======
||||||| merged common ancestors
  
=======

>>>>>>> 10888e314c177e22add2cafaaed04104971f6ce7
  return jsonify()
>>>>>>> d5880648c8f3e67c9c243c220f9ae49ad3e43f82


if __name__ == '__main__':
  app.run(debug=True, use_reloader=True)


