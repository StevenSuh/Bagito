import stripe
import json

with open('credentials.json') as json_file:
  data = json.load(json_file)
  api_key = data['STRIPE_APIKEY']
  stripe.api_key = api_key

def createCustomer(email, name):
  return stripe.Customer.create(
    email=email,
    name=name,
  )

def attachPaymentMethod(payment_id, customer_id):
  response = stripe.PaymentMethod.attach(
    payment_id,
    customer=customer_id,
  )

  if response is None or response.customer != customer_id:
    return None

  return stripe.Customer.modify(
    customer_id,
    invoice_settings={
      "default_payment_method": payment_id,
    },
  )

def createSubscription(customer_id, plan_id):
  return stripe.Subscription.create(
    customer=customer_id,
    items=[{ "plan": plan_id }],
  )

def createPlanAndProduct(price):
  product = stripe.Product.create(
    name="Bagito",
    type="service",
  )
  return stripe.Plan.create(
    amount=price,
    interval="month",
    product=product.id,
    currency="usd",
  )

def deleteCustomer(customer_id):
  return stripe.Customer.delete(customer_id)

def deletePaymentMethod(payment_id, customer_id):
  response = stripe.PaymentMethod.detach(payment_id)

  if response is None or response.customer == customer_id:
    return None

  return stripe.Customer.modify(
    customer_id,
    invoice_settings={
      "default_payment_method": None,
    },
  )

def cancelSubscription(sub_id):
  return stripe.Subscription.delete(sub_id)

def chargeCustomer(amount, description, customer_id, payment_id):
  return stripe.Charge.create(
    amount=amount,
    currency='usd',
    description=description,
    customer=customer_id,
    source=payment_id,
  )

