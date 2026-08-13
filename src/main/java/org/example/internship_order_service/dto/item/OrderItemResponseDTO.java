package org.example.internship_order_service.dto.item;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponseDTO {

    private Long id;
    private Long itemId;
    private String itemName;
    private BigDecimal price;
    private int quantity;
}
