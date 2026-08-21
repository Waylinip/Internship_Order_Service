package org.example.internship_order_service.service;


import org.example.internship_order_service.dto.ItemDTO;
import org.example.internship_order_service.entity.Item;
import org.example.internship_order_service.exception.NotFoundException;
import org.example.internship_order_service.mapper.ItemMapper;
import org.example.internship_order_service.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemService itemService;

    @Test
    void update_shouldReturnUpdatedItem_whenItemExists() {

        Long id = 1L;

        ItemDTO inputDto = new ItemDTO();
        inputDto.setName("Updated Laptop");
        inputDto.setPrice(BigDecimal.valueOf(1500));

        Item existingItem = new Item();
        existingItem.setId(id);
        existingItem.setName("Old Laptop");
        existingItem.setPrice(BigDecimal.valueOf(1200));

        Item savedItem = new Item();
        savedItem.setId(id);
        savedItem.setName("Updated Laptop");
        savedItem.setPrice(BigDecimal.valueOf(1500));

        ItemDTO expectedResult = new ItemDTO();
        expectedResult.setId(id);
        expectedResult.setName("Updated Laptop");
        expectedResult.setPrice(BigDecimal.valueOf(1500));

        when(itemRepository.findById(id)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(existingItem)).thenReturn(savedItem);
        when(itemMapper.toDTO(savedItem)).thenReturn(expectedResult);

        ItemDTO result = itemService.update(id, inputDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Laptop");
        assertThat(result.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(1500));

        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(itemCaptor.capture());
        assertThat(itemCaptor.getValue().getName()).isEqualTo("Updated Laptop");
        assertThat(itemCaptor.getValue().getPrice()).isEqualByComparingTo(BigDecimal.valueOf(1500));
    }

    @Test
    void update_shouldThrowException_whenItemNotFound() {

        Long id = 99L;
        ItemDTO dto = new ItemDTO();
        dto.setName("Anything");
        dto.setPrice(BigDecimal.valueOf(100));

        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(id, dto));
    }


}
