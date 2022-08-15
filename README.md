# Introduction

Websocket peer-2-peer communication application that delivers and stores messages between 2 user tokens, working with
multiple sessions per user.

The following technologies were used:

* Spring boot
* SocketIO for client / server
* Thymeleaf
* ActiveMQ
* MongoDB

# Running it locally

* Install docker and docker-compose
* Install java 17 and maven
* Execute `mvn clean install`
* Execute `docker-compose up -d`
* Execute `java -jar target/websocket-p2p-1.0.0.jar`
* Browse `http://localhost:8080/`
* Browse `http://localhost:8081/actuator/health`
* Browse `http://localhost:8081/actuator/info` to get the build info


# Technical details

### API's implemented
* POST /api/v1/users - to create a new user token
* GET /api/v1/messages/{authenticatedUserToken}/{userToken} - sending the tokens belonging to the users in the conversation


### Multiple connections for the same user tokens
The application allows the frontend to connect to a websocket port that facilitates the messaging delivery.
Once the frontend connects to the websocket, a SessionId is persisted in the database, which will allow
multiple connections for the same user token.

### The decision of having a single ActiveMQ consumer instead of following a multiple pub/sub pattern
The reason for having one single ActiveMQ consumer that store and route the messages was taken for the following
reason:
* extensibility: having the routing logic inside the code allows more complex routing in the future, e.g: rooms, channels

### Configuration
The application contains the following mandatory configuration properties:
* `spring.data.mongodb.uri` - example: `mongodb://admin:admin@127.0.0.1:27017/websocket-p2p?authSource=admin`
* `spring.artemis.broker-url` - example: `tcp://localhost:61616`
* `spring.artemis.user`
* `spring.artemis.password`

# Architecture
![Screenshot](websource-p2p.png)


# Testing
The testing strategy for the application was a classical mockito testing, the only integration tests were performed
for the database layer by starting an in-memory mongodb instance.
The integration tests for the controller / websocket server were not considered during the implementation.

# Scalability

Due to the limitations of the SocketIO server which currently keeps the connections in memory, socket.io recommends a
sticky-session approach for having a multi instance deployments.

# Aspects that were not considered or fine tweaked during implementation
* The authentication / old messages retrieval is done within a single HTTP call; it would have been more clean to have 2 HTTP endpoints.
* Logging aspects: no json logging, no trace identifiers, etc.
* The frontend part is an old-fashioned jquery implementation
* No integration tests for websocket / activemq part
