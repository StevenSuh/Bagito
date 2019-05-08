from db import db

class User(db.Model):
  id = db.Column(db.Integer, primary_key=True)

  # user info
  name = db.Column(db.String(256), nullable=False)
  password = db.Column(db.String(256), nullable=False)
  email = db.Column(db.String(256), nullable=False, unique=True)
  city = db.Column(db.String(256))
  state = db.Column(db.String(256))

  # obfuscated payment info only readable by stripe
  stripe_token = db.Column(db.String(256))

class Bag(db.Model):
  id = db.Column(db.Integer,primary_key=True)

  # bag info
  current_user = db.Column(db.Integer))
  rental_id = db.Column(db.Integer))

class Rental(db.Model):
  id = db.Column(db.Integer,primary_key=True)
  
  # rental info 
  location = db.Column(db.String(256))
  rental_date = db.Column(__)
  bag_id = db.