from flask import Flask, jsonify, request
from db import app, db
from db.models import User, Bag, Rental
from sqlalchemy.exc import IntegrityError

import config
import defs
import hashlib
import services.gsheets

# temporary index route
@app.route('/')
def index():
  return config.PASSWORD_SALT

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

# input: query, index
# output: data
@app.route('/api/partner', methods=['GET'])
def partner():
  query = request.args.get('query', None)
  index = request.args.get('index', 0)

  values = services.gsheets.get_partner_list(query, index)
  return jsonify(data=values)

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
def return():
  user_id = request.form['user_id']
  bag_id = request.form['bag_id']
  bin_id = request.form['bin_id']

  current_bag = Bag.query.filter_by(id=bag_id)
  
  


if __name__ == '__main__':
  app.run(debug=True, use_reloader=True)


