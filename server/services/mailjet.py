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

def send_email(email, name, code):
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
  print(result.status_code)
  print(result.json())

  return result.status_code
