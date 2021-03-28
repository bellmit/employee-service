 #!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE USER workmotion WITH PASSWORD 'workmotion' SUPERUSER;
    CREATE DATABASE employee OWNER workmotion;
    GRANT ALL PRIVILEGES ON DATABASE employee TO workmotion;
    CREATE DATABASE employee_camunda OWNER workmotion;
    GRANT ALL PRIVILEGES ON DATABASE employee_camunda to workmotion;
EOSQL
