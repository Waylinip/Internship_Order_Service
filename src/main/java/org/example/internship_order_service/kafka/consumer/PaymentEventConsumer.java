package org.example.internship_order_service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.internship_order_service.entity.OrderStatus;
import org.example.internship_order_service.kafka.event.PaymentEvent;
import org.example.internship_order_service.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "create-payment", groupId = "order-service-group")
    public void handlePaymentEvent(PaymentEvent event) {
        log.info("Received payment event: {}", event);

        OrderStatus newStatus = "SUCCESS".equals(event.status())
                ? OrderStatus.PAID
                : OrderStatus.CANCELLED;

        orderService.updateOrderStatus(event.orderId(), newStatus);
    }
}
