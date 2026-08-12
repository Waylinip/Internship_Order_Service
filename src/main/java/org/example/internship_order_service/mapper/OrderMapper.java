package org.example.internship_order_service.mapper;

import jakarta.persistence.MappedSuperclass;

import org.example.internship_order_service.dto.order.OrderRequestDTO;
import org.example.internship_order_service.dto.order.OrderResponseDTO;
import org.example.internship_order_service.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    @Mapping(target = "totalPrice", source = "price")
    OrderResponseDTO toResponseDto(Order order);

    @Mapping(target = "status", constant = "CREATED")
    Order toEntity(OrderRequestDTO orderRequestDTO);

}
