import stripe

stripe.api_key = 'sk_test_YTD8UBFrOtzhiF4nvu7HCATK00cZ342Yeq'

def createCustomer(name,email):
    return stripe.Customer.create(
        email = email,
        name = name
    )

def createSubscription(name,email)
    

def createPlan(name,price):
    stripe.Plan.create(
        amount = price,
        interval = "month",
        product = {
            "name": "Bagito"
        },
        currency = "usd"
    )