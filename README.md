Simple chat for explaining microservices
---

Elements:
- message-service is a websocket handler responsible for receiving messages and relaying them to all clients
- censorship-service is used by message-service to filter messages
- discovery-service is plain Eureka application providing service discovery
- dice-service is a slow random number generator, type "/roll" and see for yourself
- ui

References:
- https://github.com/monkey-codes/java-reactive-chat