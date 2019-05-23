
import stripe
"""
with open('credentials.json') as json_file:
  data = json.load(json_file)
  api_key = data['STRIPE_APIKEY']
  stripe.api_key = api_key
"""

stripe.api_key = 'sk_test_YTD8UBFrOtzhiF4nvu7HCATK00cZ342Yeq' 
def createPlanandProduct():
    price = 5
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

def createCustomer():
    name = 'Priya'
    email = 'prajarat@ucsc.edu'
    return stripe.Customer.create(
        email = email,
        name = name
    )

def createSubscription(plan_id,user_id):
   
    return stripe.Subscription.create(
        customer = user_id,
        items = [
            {
                "plan": plan_id,
            },
        ]
    )
if __name__ == "__main__":
    customer = createCustomer()
    print(customer.id)
    payment_method = stripe.PaymentMethod.attach(
        'pm_card_visa',
         customer = customer.id
    )
    plan = createPlanandProduct()
    createSubscription(plan.id,customer.id)

