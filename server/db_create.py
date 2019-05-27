from db import db
from db.models import User, Bag, Rental, Bin, Returned, Plan

import defs
import services.stripe

db.create_all()

price = defs.DEFAULT_PLAN_PRICE
response = services.stripe.createPlanAndProduct(price)
if response is None:
  raise Exception('Failed to create plan')

new_plan = Plan(stripe_plan_token=response.id)
db.session.add(new_plan)

# test bag
new_bag = Bag(qrcode_id="bagito")
db.session.add(new_bag)

db.session.commit()

print("DB created.")
