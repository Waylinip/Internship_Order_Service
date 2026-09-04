package org.example.internship_order_service.kafka.event;

public record PaymentEvent(
        String eventType,
        Long orderId,
        String status
) {
}
