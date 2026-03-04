Docker commands:

for bringing docker containers down: docker-compose down
for bringing docker containers up: docker-compose up -d

then check: http://localhost:8080/ui/clusters/local/brokers

How We Are Leveraging Avro:-

By introducing Avro and the Confluent Schema Registry into project, 
we solve both of those problems and add enterprise-grade safety.

The Ironclad Contract: 
PaymentEvent.avsc file is the absolute law. 
It defines exactly what a payment is. 
Because both your payment-ingestion module and your clearing-settlement-engine
module rely on the common-schemas dependency, 
it is impossible for them to disagree on what a payment looks like.

Massive Performance Gains: 
When your ingestion app sends a payment, 
Avro looks at the schema, strips away all the field names, 
and translates the actual values into raw binary. K
afka only stores the compact binary. 
This means your payment engine can process millions of transactions much faster and cheaper.

Auto-Generated Boilerplate: 
You don't have to write boring Java classes with dozens of getters, setters, and builders. 
As you saw when you ran mvn clean compile,
Avro reads your short .avsc file and auto-generates the massive, perfect PaymentEvent.java class for you.

Schema Evolution (Future-Proofing): 
Let's say next year you want to add a "countryCode" field to your payments. 
With the Schema Registry, you can deploy this change safely. 
The registry ensures "backward and forward compatibility," 
meaning your older microservices won't crash when they encounter the new field; 
they will just safely ignore it.

Phase 1 Summary: The Ingestion Layer
You successfully built the "front door" of your banking engine. Here is a breakdown of what we accomplished:

1. The Strict Data Contract (Avro)
   Instead of just throwing loose JSON around, we created the common-schemas module. We wrote the PaymentEvent.avsc file, which acts as an ironclad legal contract for what a payment must look like. If a system tries to send a payment missing a currency or an amount, the system will reject it immediately.

2. The Kafka Producer (payment-ingestion)
   You built a highly optimized Spring Boot application using Java 21 Virtual Threads.

The Controller: You created a REST API (POST /api/v1/payments) that listens for incoming payment requests from external clients (like your Talend API Tester).

The Producer: You wrote the PaymentProducer class. Its job is to take that incoming JSON, wrap it safely into our strict Avro format, and fire it off to the Kafka server.

3. The Topic and The Message

Did we create a topic? Yes! In Kafka, if a producer tries to send a message to a topic that doesn't exist, Kafka will automatically create it. When you fired that first successful request from Talend, Kafka saw the destination was payments-raw, auto-created the topic, and set up the partitions.

Is the message ready to be consumed? Yes! Your TXN-9999 payment was compressed into a tiny 143-byte binary file and written to the broker's disk. It is currently sitting inside Partition 0 of the payments-raw topic, perfectly safe, just waiting for the next part of our system (the Clearing Engine) to pick it up and process it.