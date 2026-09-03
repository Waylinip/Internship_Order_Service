package org.example.internship_order_service.mapper;

import org.example.internship_order_service.dto.ItemDTO;
import org.example.internship_order_service.entity.Item;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDTO toDTO(Item item);

    Item toEntity(ItemDTO itemDTO);
}
