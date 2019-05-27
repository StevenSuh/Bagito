# mysql+pymysql://<db_user>:<db_pass>@/<db_name>?unix_socket=/cloudsql/<INSTANCE_NAME>
# to access Google Cloud SQL locally 'mysql+pymysql://root:power2sustain@/bagito?127.0.0.1:3306'
# localhost 'sqlite:///test.db'
SQLALCHEMY_DATABASE_URI = 'mysql+pymysql://<db_user>:<db_password>@/<db_name>?unix_socket=/cloudsql/bagito:us-west1:bagito-database'
SQLALCHEMY_POOL_RECYCLE = 3600
SQLALCHEMY_TRACK_MODIFICATIONS = False

PASSWORD_SALT = 'temp'

