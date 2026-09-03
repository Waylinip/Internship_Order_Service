package org.example.internship_order_service.dto.order;

import lombok.Data;
import org.example.internship_order_service.dto.UserDTO;
import org.example.internship_order_service.dto.item.OrderItemResponseDTO;
import org.example.internship_order_service.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {

    private Long id;
    private Long userId;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private List<OrderItemResponseDTO> orderItems;
    private UserDTO user;
}
