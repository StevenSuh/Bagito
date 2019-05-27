## Instructions
`pip install -r requirements.txt` to install dependencies - you need to run this only once

`python db_create.py` to create database locally - you need to run this only once

`python main.py` to run the server - you can access to server at http://localhost:5000

`gcloud app deploy --project bagito` to deploy - if you haven't, download (gcloud sdk)[https://cloud.google.com/sdk/docs/]

## Notes
Refer to 'Tech Design Doc' in team drive for API specifications

If `python -V` returns a version lower 3, then use `pip3` over `pip` and `python3` over `python`

Get `credentials.json` file from project owner
