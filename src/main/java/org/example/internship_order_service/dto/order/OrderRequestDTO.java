package org.example.internship_order_service.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.internship_order_service.dto.item.OrderItemRequestDTO;

import java.util.List;

@Data
public class OrderRequestDTO {

    @NotNull
    private Long userId;

    @Valid
    @NotNull
    @Size(min = 1)
    private List<OrderItemRequestDTO> orderItems;
}