# Bulk Payment Clearing & Settlement Engine

A high-performance, distributed, banking-grade payment processing engine built with **Java 21**, **Spring Boot 3.x**, and **Apache Kafka**.

This project demonstrates how to use Kafka not just as a simple message bus, but as a robust distributed log and state store. It handles the ingestion, validation, clearing, and settlement of bulk corporate payments, ensuring zero data loss and strict schema enforcement.



## 🚀 Core Banking Features Implemented

1. **High-Throughput Ingestion (Virtual Threads):** Leverages Java 21 Virtual Threads to handle massive concurrent partition consumption without the memory overhead of traditional platform threads.
2. **Exactly-Once Semantics (EOS):** Configured with `processing.guarantee=exactly_once_v2` to ensure the "Read-Process-Write" cycle is atomic. A $1M payment will never be processed twice, even if a node crashes mid-flight.
3. **Schema Evolution (Avro & Schema Registry):** Uses Confluent Schema Registry to enforce strict data contracts. Malformed payloads are rejected before they pollute downstream systems.
4. **Log Compaction for Reference Data:** Keeps only the latest state of static/slow-moving data (like FX currency rates) in memory for lightning-fast lookups upon restart.
5. **Stateful Stream Processing (Kafka Streams):** - **KTables:** Maintains the real-time "Current Balance" of corporate accounts using RocksDB state stores.
    - **Tumbling Windows:** Aggregates total outbound liquidity volumes in 10-minute windows for regulatory reporting.

## 🏗️ Project Structure

This is a multi-module Maven project separated by domain concerns:

* `infrastructure/`: Contains the `docker-compose.yml` to spin up Kafka, Zookeeper/KRaft, Schema Registry, and Kafka Connect locally. Also holds sample payment CSV files.
* `common-schemas/`: The single source of truth for our data models. Contains Avro (`.avsc`) schemas that auto-generate Java classes during the build process.
* `reference-data-service/`: A Spring Boot producer that continuously streams static data (like `USD_TO_EUR` exchange rates) to a compacted Kafka topic.
* `payment-ingestion/`: Reads raw payments, performs initial business validation, maps them to Avro, and partitions them optimally using Virtual Threads.
* `clearing-settlement-engine/`: The Kafka Streams application. Manages KTables, executes windowed aggregations, and enforces Exactly-Once processing guarantees.

## 🛠️ Prerequisites

Before you begin, ensure you have the following installed on your machine:
* **Java 21** (JDK 21)
* **Maven 3.8+**
* **Docker & Docker Compose** (for running the Kafka ecosystem locally)

## 🏃‍♂️ Getting Started

*(Instructions to be added as we build out the infrastructure and modules!)*