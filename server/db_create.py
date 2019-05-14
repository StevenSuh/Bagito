from db import db
from db.models import User, Bag, Rental, Bin, Returned

db.create_all()
print("DB created.")
