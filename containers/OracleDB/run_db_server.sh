#!/bin/bash

docker stop webDB 2>/dev/null
docker rm webDB 2>/dev/null

# Run Oracle container
docker run --name webDB -d -p 1523:1521 -e ORACLE_PASSWORD=api_test gvenzl/oracle-free

# Copy SQL scripts into container
docker cp create_user.sql webDB:/tmp/create_user.sql
docker cp create_tables.sql webDB:/tmp/create_tables.sql
docker cp create_seq.sql webDB:/tmp/create_seq.sql

echo "Waiting for Oracle to be ready..."
until docker logs webDB 2>&1 | grep -q "DATABASE IS READY TO USE"; do
  sleep 2
done
echo "Oracle is ready!"

docker exec -it webDB sqlplus sys/api_test@localhost:1521/FREEPDB1 as sysdba @/tmp/create_user.sql

sleep 2

docker exec -it webDB sqlplus api_test/api_test@localhost:1521/FREEPDB1 @/tmp/create_tables.sql
sleep 1
docker exec -it webDB sqlplus api_test/api_test@localhost:1521/FREEPDB1 @/tmp/create_seq.sql
sleep 1

echo "Setup complete. You can now connect with: api_test/api_test@localhost:1521/FREEPDB1"
