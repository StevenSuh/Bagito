# mysql+pymysql://<db_user>:<db_pass>@/<db_name>?unix_socket=/cloudsql/<INSTANCE_NAME>
# to access Google Cloud SQL locally, replace with'mysql+pymysql://root:power2sustain@/bagito?127.0.0.1:3306' after setting up cloud sql proxy
# localhost 'sqlite:///test.db'
SQLALCHEMY_DATABASE_URI = 'mysql+pymysql://<db_user>:<db_pass>@/<db_name>?unix_socket=/cloudsql/bagito:us-central1:bagito-db'
SQLALCHEMY_POOL_RECYCLE = 3600
SQLALCHEMY_TRACK_MODIFICATIONS = False

PASSWORD_SALT = 'temp'

