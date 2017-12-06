# spring-batch-demo
App for demo of spring batch

##Script for granting permissions to spring_batch user to micro_machines.spring_batch:
grant ALL PRIVILEGES on ALL SEQUENCES IN SCHEMA spring_batch to spring_batch
grant ALL PRIVILEGES on database micro_machines to spring_batch
GRANT ALL PRIVILEGES ON SCHEMA spring_batch TO spring_batch

##Script that is executed is available at:
org/springframework/batch/core/schema-postgresql.sql