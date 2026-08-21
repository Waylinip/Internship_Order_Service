package org.example.internship_order_service.dto.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemRequestDTO {

    @NotNull
    private Long itemId;

    @Min(1)
    private int quantity;
}
