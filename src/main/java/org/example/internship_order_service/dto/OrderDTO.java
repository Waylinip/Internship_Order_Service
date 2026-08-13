package org.example.internship_order_service.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.example.internship_order_service.entity.OrderItem;
import org.example.internship_order_service.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {

    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private OrderStatus status;

    private BigDecimal price;

    private LocalDateTime createdAt;

    @Valid
    @NotNull
    @Size(min = 1)
    private List<OrderItemDTO> orderItems;

    private UserDTO user;


}
