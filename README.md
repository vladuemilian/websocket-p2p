# Introduction

Websocket peer-2-peer communication application that delivers and stores messages between 2 user tokens.

The following technologies were used:

* Spring boot
* SocketIO for client / server
* Thymeleaf
* ActiveMQ
* MongoDB

# Technical details

### Multiple connections for the same user tokens
The application allows the frontend to connect to a websocket port that facilitates the messaging delivery.
Once the frontend connects to the websocket, a SessionId is persisted in the database, which will allow
multiple connections for the same user token.

### The decision of having a single ActiveMQ consumer instead of following a multiple pub/sub pattern
The reason for having one single ActiveMQ consumer that store and route the messages was taken for the following
reason:
* extensibility: having the routing logic inside the code allows more complex routing in the future, e.g: rooms, channels

# Architecture
....

# Testing
The testing strategy for the application was a classical mockito testing, the only integration tests were performed
for the database layer by starting an in-memory mongodb instance.
The integration tests for the controller / websocket server were not considered during the implementation.

# Scalability

Due to the limitations of the SocketIO server which currently keeps the connections in memory, socket.io recommends a
sticky-session approach for having a multi instance deployments.

# Aspects that were not considered or fine tweaked during implementation
* Logging aspects: no json logging, no trace identifiers, etc.
* The frontend part is an old-fashioned jquery implementation
