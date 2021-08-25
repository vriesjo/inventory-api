# Inventory Management API

This rest api is used by the inventory-webapp to provide inventory information.

It's a Spring MVC API includes 

- the inventory CRUD operations supported by PostgresSQL.
- the inventory reservation functionality supported by Redis.  

Prerequisites: Java 11, Maven.

### Design
The domain model has 3 entities: 
- Product, Supplier and Category 

With the following relations:
- Product ManyToOne Supplier 
- Product ManyToOne Category

### Features:
- Web Service endpoint
- PostgreSQL support
- Liquibase support
- inventory CRUD operations with Sping JPA
- reservation feature with Redis  

### To run locally:
First start the PostgreSQL database:

`cd docker/postgres/`

`docker-compose up -d `  

then start the Redis instance:

`cd docker/redis/`

`docker-compose up -d ` 

and finally start the project, which will also execute the Liquibase changesets:

`./mvnw spring-boot:run`

### Run dockerized version
 WIP

### Documentation
- http://localhost:7070/swagger-ui-custom.html