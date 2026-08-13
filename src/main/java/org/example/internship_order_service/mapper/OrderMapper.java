package org.example.internship_order_service.mapper;

import jakarta.persistence.MappedSuperclass;
import org.example.internship_order_service.dto.OrderDTO;
import org.example.internship_order_service.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    OrderDTO toDTO(Order order);

    Order toEntity(OrderDTO orderDTO);

}
