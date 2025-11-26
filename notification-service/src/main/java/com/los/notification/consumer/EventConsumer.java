package main.java.com.los.notification.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventConsumer {

    @KafkaListener(topics = "customer-events", groupId = "notification-service")
    public void consumeCustomerEvents(String message) {
        log.info("📧 Received customer event: {}", message);
        // Simulate sending notification (email/SMS/push)
        log.info("✅ Notification sent for customer event");
    }

    @KafkaListener(topics = "loan-events", groupId = "notification-service")
    public void consumeLoanEvents(String message) {
        log.info("📧 Received loan event: {}", message);
        // Simulate sending notification
        log.info("✅ Notification sent for loan event");
    }

    @KafkaListener(topics = "disbursement-events", groupId = "notification-service")
    public void consumeDisbursementEvents(String message) {
        log.info("📧 Received disbursement event: {}", message);
        // Simulate sending notification
        log.info("✅ Notification sent for disbursement event");
    }
}
