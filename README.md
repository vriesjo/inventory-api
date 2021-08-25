# Inventory Management API

This Rest API is used by the inventory-webapp and provides inventory information.

It's a Spring MVC API and it includes 

- the inventory CRUD operations supported by PostgresSQL.
- the inventory reservation functionality supported by Redis.  

Prerequisites: Java 11, Maven.

### Design
The domain model contains 3 entities: 
- Product, Supplier and Category 

With the following relations:
- Product ManyToOne Supplier 
- Product ManyToOne Category

### Features:
- Web Service endpoint
- PostgreSQL support
- Liquibase support
- Inventory CRUD operations with Spring JPA
- Reservation flow with Redis. 

The TTL value for the Redis cache reservation keys (possibly a shopping cart) is currently 30 minutes (1800 seconds). 
This implies that each keys will automatically expire after 1800 seconds.  

This modifying the `spring.redis.ttl` this can be changed. For testing purpose, it can be convenient.    

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