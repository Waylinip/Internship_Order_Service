package org.example.internship_order_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemDTO {

    private Long id;

    @NotBlank
    private String name;

    @NotNull
    @Positive
    private BigDecimal price;
}
