
import stripe
"""
with open('credentials.json') as json_file:
  data = json.load(json_file)
  api_key = data['STRIPE_APIKEY']
  stripe.api_key = api_key
"""
def createCustomer():
    name = request.args.get('name',None)
    email = request.args.get('email',None)
    return stripe.Customer.create(
        email = email,
        name = name
    )

def attachPaymentMethod(payment_id,customer_id):
    payment_method = stripe.PaymentMethod.attach(
         payment_id,
         customer = customer.id
    )

def createSubscription():
    user_id = request.args.get('user_id',None)
    plan_id = request.args.get('plan_id',None)
    return stripe.Subscription.create(
        customer = user_id,
        items = [
            {
                "plan": plan_id,
            },
        ]
    )

def cancelSubscription(sub_id):
    stripe.Subscription.delete(sub_id)
def createPlanandProduct():
    price = request.args.get('price',None)
    product = stripe.Product.create(
      name = "Bagito",
      type = "service",

    )
    return stripe.Plan.create(
        amount = price,
        interval = "month",
        product = product.id,
        currency = "usd",
    )

def createPaymentMethod():
    return stripe.PaymentMethod.create(
        type = 'card',
        billing_details = {
          address :{
            'city': request.form['city'],
            'country': request.form['country'],
            'line1': request.form['line1'],
            'line2': request.form['line2'],
            'postal_code': request.form['postal_code'],
            'state': request.form['state']
          },
          'email': request.form['email'],
          'name': request.form['name'],
          'phone': request.form['phone']


        },
        card = {
            'number': request.form['number'],
            'exp_month': request.form['exp_month'],
            'exp_year': request.form['exp_year'],
            'cvc': request.form['cvc']
        },
    )

def updatePaymentMethod(paymentmethod_id ):
    return stripe.PaymentMethod.modify(
        paymentmethod_id,
        type = 'card',
        billing_details = {
          address : {
            'city': request.form['city'],
            'country': request.form['country'],
            'line1': request.form['line1'],
            'line2': request.form['line2'],
            'postal_code': request.form['postal_code'],
            'state' : request.form['state']
          },
          'email': request.form['email'],
          'name': request.form['name'],
          'phone': request.form['phone']
        },
        card = {
            'number': request.form['number'],
            'exp_month': request.form['exp_month'],
            'exp_year': request.form['exp_year'],
            'cvc': request.form['cvc']
        },
    )

def unSavePaymentMethod(payment_id):
    return stripe.PaymentMethod.detatch(payment_id)

def cancelSubscription(sub_id):
    return stripe.Subscription.delete(sub_id)

