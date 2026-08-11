package org.example.internship_order_service.mapper;

import org.example.internship_order_service.dto.OrderItemDTO;
import org.example.internship_order_service.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "itemName", source = "item.name")
    @Mapping(target = "price", source = "item.price")
    OrderItemDTO toDto(OrderItem orderItem);

    @Mapping(target = "item.id", source = "itemId")
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(OrderItemDTO orderItemDto);
}
