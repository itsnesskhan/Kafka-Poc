# Kafka Setup on Ubuntu

This guide explains how to install and run **Apache Kafka locally on Ubuntu**, including starting Zookeeper, Kafka broker, and testing with console producer/consumer.

---

## 1. Prerequisites

- Ubuntu 20.04+
- Java 11 or 17 installed

Check Java:
```bash
java -version
```

If not installed:
```bash
sudo apt update
sudo apt install openjdk-11-jdk -y
```

---

## 2. Download Kafka

```bash
wget https://downloads.apache.org/kafka/3.6.1/kafka_2.13-3.6.1.tgz
tar -xvzf kafka_2.13-3.6.1.tgz
cd kafka_2.13-3.6.1
```

---

## 3. Start Zookeeper

Kafka requires Zookeeper (for older & non-KRaft mode).

```bash
cd kafka_2.13-3.6.1/bin
./zookeeper-server-start.sh ../config/zookeeper.properties
```

Keep this terminal **running**.

---

## 4. Start Kafka Server

Open a **new terminal**:

```bash
cd kafka_2.13-3.6.1/bin
./kafka-server-start.sh ../config/server.properties
```

Kafka will start on:
```
localhost:9092
```

---

## 5. Create a Kafka Topic

```bash
./kafka-topics.sh \
--bootstrap-server localhost:9092 \
--create \
--topic order-created \
--partitions 1 \
--replication-factor 1
```

Verify topic:
```bash
./kafka-topics.sh --bootstrap-server localhost:9092 --list
```

---

## 6. Start Console Producer

```bash
./kafka-console-producer.sh \
--bootstrap-server localhost:9092 \
--topic order-created
```

Type messages and press Enter to publish.

---

## 7. Start Console Consumer

Open **another terminal**:

```bash
./kafka-console-consumer.sh \
--bootstrap-server localhost:9092 \
--topic order-created \
--from-beginning
```

You will see messages produced from the producer.

---

## 8. Spring Boot Local Kafka Config

```properties
spring.kafka.bootstrap-servers=localhost:9092
```

---

## 9. Common Issues

### Port already in use
```bash
lsof -i :9092
kill -9 <PID>
```

### Zookeeper not running
Kafka will fail if Zookeeper is not started first.

---

## 10. Stop Kafka & Zookeeper

Press **CTRL + C** in both terminals.

---

## 11. Next Steps

- Connect Spring Boot Producer & Consumer
- Enable JSON serialization
- Use `__TypeId__` for multi-service events

---

✅ Kafka is now running successfully on Ubuntu

