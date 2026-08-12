package org.example.internship_order_service.service;

import org.example.internship_order_service.client.UserServiceClient;
import org.example.internship_order_service.dto.item.OrderItemRequestDTO;
import org.example.internship_order_service.dto.order.OrderRequestDTO;
import org.example.internship_order_service.dto.order.OrderResponseDTO;
import org.example.internship_order_service.entity.Item;
import org.example.internship_order_service.entity.Order;
import org.example.internship_order_service.exception.NotFoundException;
import org.example.internship_order_service.mapper.OrderMapper;
import org.example.internship_order_service.repository.ItemRepository;
import org.example.internship_order_service.repository.OrderRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_shouldReturnOrderResponseDTO_whenValidRequest() {

        OrderItemRequestDTO itemRequest = new OrderItemRequestDTO();
        itemRequest.setItemId(1L);
        itemRequest.setQuantity(2);

        OrderRequestDTO request = new OrderRequestDTO();
        request.setUserId(10L);
        request.setOrderItems(List.of(itemRequest));

        Item item = new Item();
        item.setId(1L);
        item.setName("Laptop");
        item.setPrice(BigDecimal.valueOf(1200));

        Order mappedOrder = new Order();
        mappedOrder.setUserId(10L);

        Order savedOrder = new Order();
        savedOrder.setId(100L);
        savedOrder.setUserId(10L);

        OrderResponseDTO expectedResponse = new OrderResponseDTO();
        expectedResponse.setId(100L);

        when(orderMapper.toEntity(request)).thenReturn(mappedOrder);
        when(itemRepository.findAllById(List.of(1L))).thenReturn(List.of(item));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toResponseDto(savedOrder)).thenReturn(expectedResponse);
        when(userServiceClient.getUserById(10L)).thenReturn(null);


        OrderResponseDTO result = orderService.createOrder(request);


        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void getOrderById_shouldReturnOrder_whenOrderExists() {

        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setUserId(5L);

        OrderResponseDTO expectedDto = new OrderResponseDTO();
        expectedDto.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toResponseDto(order)).thenReturn(expectedDto);
        when(userServiceClient.getUserById(5L)).thenReturn(null);

        OrderResponseDTO result = orderService.getOrderById(orderId);

        assertThat(result.getId()).isEqualTo(orderId);
        verify(orderRepository).findById(orderId);
    }

    @Test
    void getOrderById_shouldThrowException_whenOrderNotFound() {
        Long orderId = 99L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrderById(orderId));
    }
}
