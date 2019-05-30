from mailjet_rest import Client
import json

service = None

def build_service():
  global service
  with open('credentials.json') as json_file:
    data = json.load(json_file)

    api_key = data['MJ_APIKEY_PUBLIC']
    api_secret = data['MJ_APIKEY_PRIVATE']

    service = Client(auth=(api_key, api_secret), version='v3.1')

def send_reset_pw_email(email, name, code):
  if service is None:
    build_service()

  data = {
    'Messages': [
      {
        "From": {
          "Email": "stevenesuh@gmail.com", # TODO: update from-email
          "Name": "Bagito"
        },
        "To": [
          {
            "Email": email,
            "Name": name
          }
        ],
        "Subject": "Bagito - Forgot Password",
        "TextPart": "To " + name +
          ",\n\nYou have requested a code for password reset.\n\nThe code is " + code +
          ". The code will expire in 1 hour.\n\nBest,\nBagito"
      }
    ]
  }

  result = service.send.create(data=data)
  return result.status_code

def send_rental_reminder_email(recipients):
  if service is None:
    build_service()

  data = {
    'Messages': [
      'From': {
        'Email': 'stevenesuh@gmail.com',
        'Name': 'Bagito',
      },
      'To': recipients,
      'Subject': 'Bagito - Bag Rental Reminder',
      'TextPart': "To {{var:NAME}},\n\nThis is a reminder that you have rented a bag {{var:DAYS}} days ago. After 15th day, you will be charged the bag'si full price, allowing you to keep the bag permanently.\n\nTo return the bag, visit our partner vendor locations.\n\nBest,\nBagito",
    ],
  }

  result = mailjet.send.create(data=data)
  return result.status_code


