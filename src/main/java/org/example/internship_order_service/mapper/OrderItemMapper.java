package org.example.internship_order_service.mapper;

import org.example.internship_order_service.dto.item.OrderItemRequestDTO;
import org.example.internship_order_service.dto.item.OrderItemResponseDTO;
import org.example.internship_order_service.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "itemName", source = "item.name")
    @Mapping(target = "price", source = "item.price")
    OrderItemResponseDTO toResponseDto(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(OrderItemRequestDTO dto);
}
