from db import db

class User(db.Model):
  id = db.Column(db.Integer, primary_key=True)

  # user info
  name = db.Column(db.String(256), nullable=False)
  password = db.Column(db.String(256), nullable=False)
  email = db.Column(db.String(256), nullable=False, unique=True)
  city = db.Column(db.String(256))
  state = db.Column(db.String(256))

  # forgot password
  code = db.Column(db.String(4))
  code_exp = db.Column(db.DateTime)

  # obfuscated payment info only readable by stripe
  stripe_customer_token = db.Column(db.String(256))
  stripe_payment_token = db.Column(db.String(256))
  stripe_subscription_token = db.Column(db.String(256))
  has_paid = db.Column(db.Integer, default=0)

class Bag(db.Model):
  id = db.Column(db.Integer, primary_key=True)

  # bag info
  current_user = db.Column(db.Integer)
  rental_id = db.Column(db.Integer)
  qrcode_id = db.Column(db.String(256), unique=True)
  bin_id = db.Column(db.String(256))

class Rental(db.Model):
  id = db.Column(db.Integer, primary_key=True)

  location = db.Column(db.String(256))
  rental_date = db.Column(db.DateTime)
  bag_id = db.Column(db.Integer)
  returned_id = db.Column(db.Integer)
  user_id = db.Column(db.Integer)

class Bin(db.Model):
  id = db.Column(db.Integer, primary_key=True)

  qrcode_id = db.Column(db.String(256))
  location = db.Column(db.String(256))

class Returned(db.Model):
  id = db.Column(db.Integer, primary_key=True)

  location = db.Column(db.String(256))
  bag_id = db.Column(db.Integer)
  return_date = db.Column(db.DateTime)
  bin_id = db.Column(db.Integer)
  user_id = db.Column(db.Integer)

class Plan(db.Model):
  id = db.Column(db.Integer, primary_key=True)

  stripe_plan_token = db.Column(db.String(256))

