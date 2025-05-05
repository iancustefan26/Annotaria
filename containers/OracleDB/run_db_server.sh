docker run --name webDB -d -p 1523:1521 -e ORACLE_PASSWORD=api_test gvenzl/oracle-free

# here copy scripts for populating after creating them

docker cp create_user.sql webDB:/tmp/
docker cp create_seq.sql webDB:/tmp/ 
docker cp create_tables.sql webDB:/tmp/ 

echo "Waiting for Oracle to be ready..."
until docker logs webDB 2>&1 | grep -q "DATABASE IS READY TO USE"; do
  sleep 2
done
echo "Oracle is ready!"

# here copy execute the scripts for populating the database inside docker image
#

docker exec -it webDB sqlplus sys/api_test as sysdba @/tmp/create_user.sql
sleep 1
docker exec -it webDB sqlplus api_test/api_test @/tmp/create_tables.sql 
sleep 1 
docker exec -it webDB sqlplus api_test/api_test @/tmp/create_seq.sql
sleep 1


echo "For more info about the docker image: https://hub.docker.com/r/gvenzl/oracle-free"
echo "Connect with api_test/api_test"
