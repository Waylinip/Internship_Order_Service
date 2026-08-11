package org.example.internship_order_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {

    private Long id;

    @NotNull()
    private Long itemId;

    @Size()
    private String itemName;

    @Positive()
    private BigDecimal price;

    @Min(value = 1)
    private int quantity;
}
