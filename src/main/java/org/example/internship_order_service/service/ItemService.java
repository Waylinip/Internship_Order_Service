package org.example.internship_order_service.service;

import lombok.RequiredArgsConstructor;
import org.example.internship_order_service.dto.ItemDTO;
import org.example.internship_order_service.entity.Item;
import org.example.internship_order_service.exception.NotFoundException;
import org.example.internship_order_service.mapper.ItemMapper;
import org.example.internship_order_service.repository.ItemRepository;
import org.example.internship_order_service.repository.OrderRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {


    public static final String ITEM_NOT_FOUND = "Item not found with id:";
    public static final String ID_CANNOT_BE_NULL = "Id cannot be null";
    public static final String DTO_CANNOT_BE_NULL = "ItemDTO cannot be null";

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;


    @Transactional
    public ItemDTO create(ItemDTO itemDTO) {
        if (itemDTO == null) {
            throw new IllegalArgumentException(DTO_CANNOT_BE_NULL);
        }
        Item item = itemMapper.toEntity(itemDTO);
        return itemMapper.toDTO(itemRepository.save(item));
    }

    public ItemDTO getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL);

        }
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND + id));
        return itemMapper.toDTO(item);

    }

    public List<ItemDTO> getAll() {
        List<Item> items = itemRepository.findAll();
        return items.stream().map(itemMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public ItemDTO update(Long id, ItemDTO itemDTO) {
        if (itemDTO == null) {
            throw new IllegalArgumentException(DTO_CANNOT_BE_NULL);
        }
        if (id == null) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL);
        }

        Item item = itemRepository.findById(id).
                orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND + id));

        item.setName(itemDTO.getName());
        item.setPrice(itemDTO.getPrice());

        Item itemUpdated = itemRepository.save(item);

        return itemMapper.toDTO(itemUpdated);
    }

    @Transactional
    public void delete(Long id) {
        if (id==null) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL);
        }
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND + id));
        itemRepository.delete(item);

    }

}
