# Using `__TypeId__` in Kafka (Spring Boot)

## Overview
In Spring Kafka, `__TypeId__` is a **message header** automatically added by the `JsonSerializer`. It stores the **fully qualified class name** of the object being sent to Kafka.

This header helps the consumer **deserialize JSON back into the correct Java object** without ambiguity.

---

## Why `__TypeId__` Exists

When Kafka sends data, it only understands **bytes**, not Java classes.

`__TypeId__` solves these problems:

- ✅ Tells the consumer **which class** the JSON belongs to
- ✅ Enables **polymorphic events** (multiple event types in one topic)
- ✅ Avoids manual `ObjectMapper` logic in consumers
- ✅ Prevents deserialization errors

Without it, the consumer must guess or hardcode the target class.

---

## Example Scenario

### Event Classes
```java
public class OrderEvent {
    private Long orderId;
    private String status;
}

public class PaymentEvent {
    private Long paymentId;
    private String status;
}
```

Both events are sent to the same Kafka topic.

---

## Producer Configuration

### application.yml
```yaml
spring:
  kafka:
    producer:
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

### Producer Code
```java
kafkaTemplate.send("order-created", orderId, new OrderEvent(order));
```

### What Kafka Actually Sends

- **Value** → JSON payload
- **Header** →
  ```
  __TypeId__ = com.example.event.OrderEvent
  ```

---

## Consumer Configuration (Using `__TypeId__`)

### application.yml
```yaml
spring:
  kafka:
    consumer:
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.example.event"
```

### Consumer Listener
```java
@KafkaListener(topics = "order-created")
public void consume(OrderEvent event) {
    System.out.println(event.getOrderId());
}
```

Spring Kafka reads `__TypeId__` and automatically converts JSON → `OrderEvent`.

---

## Using Multiple Event Types (Recommended)

```java
@KafkaListener(topics = "events")
public void consume(Object event) {
    if (event instanceof OrderEvent) {
        // handle order
    } else if (event instanceof PaymentEvent) {
        // handle payment
    }
}
```

This works **only because `__TypeId__` is present**.

---

## Custom Type Mapping (Optional)

Instead of full class names, you can map short names.

### Producer
```yaml
spring.json.type.mapping: order:com.example.event.OrderEvent
```

### Consumer
```yaml
spring.json.type.mapping: order:com.example.event.OrderEvent
```

Kafka Header:
```
__TypeId__ = order
```

---

## Disabling `__TypeId__` (Not Recommended)

```yaml
spring.kafka.producer.properties.spring.json.add.type.headers: false
```

⚠️ You must then manually deserialize JSON.

---

## Common Errors

### ❌ `ClassNotFoundException`
✔ Fix: Add
```yaml
spring.json.trusted.packages: "*"
```
(or restrict to your package)

### ❌ `Could not resolve type id`
✔ Fix: Ensure producer and consumer use the **same class name or mapping**

---

## When Should You Use `__TypeId__`

✔ Event-driven architecture
✔ Multiple event types per topic
✔ Microservices with shared contracts

❌ Single static payload forever

---

## Summary

- `__TypeId__` = class identifier for Kafka messages
- Added automatically by `JsonSerializer`
- Makes deserialization safe and flexible
- Essential for scalable event-driven systems

---

**Recommended**: Always keep `__TypeId__` enabled unless you have a very strong reason not to.

