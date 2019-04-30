from flask import Flask, jsonify, request
from db import db
from db.models import User
import config
import hashlib

app = Flask(__name__)

# temporary index route
@app.route('/')
def index():
  return config.PASSWORD_SALT

# input: email, password
# output: success, msg (empty if success is True)
@app.route('/api/login', methods=['POST'])
def login():
  req_data = request.get_json()

  email = req_data['email']
  password = req_data['password']

  user = User.query.filter_by(email=email).first()
  if user is None:
    return jsonify(success=False, msg='No such email')

  password_hash = hashlib.sha256(password + config.PASSWORD_SALT).hexdigest()
  if user.password != password_hash:
    return jsonify(success=False, msg='Incorrect password')

  return jsonify(success=True)

if __name__ == '__main__':
  app.run(debug=True, use_reloader=True)
