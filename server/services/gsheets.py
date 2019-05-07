import gspread
from oauth2client.service_account import ServiceAccountCredentials

SPREADSHEET_KEY = '1A4MH7sRBWUc1qEVasgmMgkZHgwWFOPBPAKXBteKf81o'
scope = ['https://www.googleapis.com/auth/spreadsheets.readonly']

service = None

def build_service():
  global service
  creds = ServiceAccountCredentials.from_json_keyfile_name('credentials.json', scope)
  service = gspread.authorize(creds)

def get_partner_list(query=None, index=0):
  if service is None:
    build_service()

  sheet = None
  try:
    sheet = service.open_by_key(SPREADSHEET_KEY)
  except gspread.exceptions.APIError:
    return {}

  worksheet = None
  if query:
    worksheet = sheet.worksheet(query)
  else:
    worksheet = sheet.get_worksheet(index)

  values = {}
  if worksheet:
    values['title'] = worksheet.title
    values['records'] = worksheet.get_all_records()

  return values

