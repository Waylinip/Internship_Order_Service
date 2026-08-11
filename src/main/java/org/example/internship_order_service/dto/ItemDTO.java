package org.example.internship_order_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class ItemDTO {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private BigDecimal price;
}
