#!/bin/bash

docker stop oracle-web 2>/dev/null
docker rm oracle-web 2>/dev/null

docker run --name oracle-web -d -p 1521:1521 -e ORACLE_PASSWORD=api_test gvenzl/oracle-free

docker cp create_user.sql oracle-web:/tmp/
docker cp create_tables.sql oracle-web:/tmp/
docker cp create_seq.sql oracle-web:/tmp/
docker cp tables_populate.sql oracle-web:/tmp/
docker cp create_logs.sql oracle-web:/tmp/
docker cp create_post_triggers.sql oracle-web:/tmp/
docker cp create_exceptions.sql oracle-web:/tmp/
docker cp create_auth_helper_func.sql oracle-web:/tmp/
docker cp create_posts_helper_func.sql oracle-web:/tmp/
docker cp feed_formulas.sql oracle-web:/tmp/
docker cp page_rank.sql oracle-web:/tmp/
docker cp create_graph_wrappers.sql oracle-web:/tmp/
docker cp create_prevent_mutating_table.sql oracle-web:/tmp/
docker cp export_statistics.sql oracle-web:/tmp/


echo "Waiting for Oracle to be ready..."
until docker logs oracle-web 2>&1 | grep -q "DATABASE IS READY TO USE"; do
  sleep 2
done
echo "Oracle is ready!"


docker exec -it oracle-web sqlplus sys/api_test as sysdba @/tmp/create_user.sql

docker exec -it oracle-web sqlplus api_test/api_test @/tmp/create_tables.sql

docker exec -it oracle-web sqlplus api_test/api_test @/tmp/create_seq.sql

docker exec -it oracle-web sqlplus api_test/api_test @/tmp/create_exceptions.sql

docker exec -it oracle-web sqlplus api_test/api_test @/tmp/create_post_triggers.sql

docker exec -it oracle-web sqlplus api_test/api_test @/tmp/feed_formulas.sql

docker exec -it oracle-web sqlplus api_test/api_test @/tmp/page_rank.sql

docker exec -it oracle-web sqlplus api_test/api_test @/tmp/create_graph_wrappers.sql

docker exec -it oracle-web sqlplus api_test/api_test @/tmp/create_prevent_mutating_table.sql

docker exec -it oracle-web sqlplus api_test/api_test @/tmp/create_posts_helper_func.sql

docker exec -it oracle-web sqlplus api_test/api_test @/tmp/create_auth_helper_func.sql

docker exec -it oracle-web sqlplus api_test/api_test @/tmp/export_statistics.sql

docker exec -it oracle-web sqlplus api_test/api_test @/tmp/tables_populate.sql

#after that DDL operations wouldn't be available anymore
# docker exec -it oracle-web sqlplus API_TEST/api_test @/tmp/create_logs.sql

echo "For more info about the docker image: https://hub.docker.com/r/gvenzl/oracle-free"
echo "Connect with api_test/api_test"
