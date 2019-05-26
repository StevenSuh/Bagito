import gspread
from oauth2client.service_account import ServiceAccountCredentials

SPREADSHEET_KEY = '1A4MH7sRBWUc1qEVasgmMgkZHgwWFOPBPAKXBteKf81o'
scope = ['https://www.googleapis.com/auth/spreadsheets.readonly']

service = None

def build_service():
  global service
  creds = ServiceAccountCredentials.from_json_keyfile_name('credentials.json', scope)
  service = gspread.authorize(creds)

def does_location_exist(city, state):
  if service is None:
    build_service()

  query = city.lower() + ", " + state.upper();

  sheet = None
  try:
    sheet = service.open_by_key(SPREADSHEET_KEY)
  except gspread.exceptions.APIError:
    return false

  worksheet = None
  try:
    worksheet = sheet.worksheet(query)
  except gspread.exceptions.WorksheetNotFound:
    return false

  return true

def get_partner_list(query=None):
  if service is None:
    build_service()

  sheet = None
  try:
    sheet = service.open_by_key(SPREADSHEET_KEY)
  except gspread.exceptions.APIError:
    return []

  worksheet = None
  if query:
    try:
      worksheet = sheet.worksheet(query)
    except gspread.exceptions.WorksheetNotFound:
      return []
  else:
    worksheet = sheet.get_worksheet(0)

  values = []
  if worksheet:
    values = worksheet.get_all_records()

  return values

