package org.example.internship_order_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {

    private Long id;

    @NotNull
    private Long itemId;

    @NotBlank
    private String itemName;

    @Positive
    private BigDecimal price;

    @Min(value = 1)
    private int quantity;
}
