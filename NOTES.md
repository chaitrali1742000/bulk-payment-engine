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