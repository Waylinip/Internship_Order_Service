package org.example.internship_order_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.example.internship_order_service.client.UserServiceClient;
import org.example.internship_order_service.dto.item.OrderItemRequestDTO;
import org.example.internship_order_service.dto.order.OrderRequestDTO;
import org.example.internship_order_service.dto.order.OrderResponseDTO;
import org.example.internship_order_service.entity.Item;
import org.example.internship_order_service.entity.Order;
import org.example.internship_order_service.entity.OrderItem;
import org.example.internship_order_service.entity.OrderStatus;
import org.example.internship_order_service.exception.NotFoundException;
import org.example.internship_order_service.mapper.OrderMapper;
import org.example.internship_order_service.repository.ItemRepository;
import org.example.internship_order_service.repository.OrderRepository;
import org.example.internship_order_service.specification.OrderSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private static final String ORDER_DTO_NULL = "OrderDTO cannot be null";
    private static final String USER_ID_NULL = "User id cannot be null";

    private static final String ORDER_NOT_FOUND = "Order not found with id";
    private static final String ITEM_NOT_FOUND = "Item not found with id";

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final OrderMapper orderMapper;
    private final UserServiceClient userServiceClient;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        if (orderRequestDTO == null) {
            throw new IllegalArgumentException(ORDER_DTO_NULL);
        }

        Order order = orderMapper.toEntity(orderRequestDTO);

        List<OrderItem> orderItems = buildOrderItems(orderRequestDTO.getOrderItems(), order);
        order.setOrderItems(orderItems);
        order.setPrice(calculateTotalPrice(orderItems));

        Order savedOrder = orderRepository.save(order);
        return mapWithUserInfo(savedOrder);
    }


    public OrderResponseDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND + orderId));
        return mapWithUserInfo(order);
    }

    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
        if(userId == null) {
            throw new IllegalArgumentException(USER_ID_NULL);
        }
        return orderRepository.getByUserId(userId).stream()
                .map(this::mapWithUserInfo)
                .toList();
    }

    public Page<OrderResponseDTO> getAll(List<OrderStatus> statuses, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        Specification<Order> specification = Specification
                .where(OrderSpecifications.hasStatuses(statuses))
                .and(OrderSpecifications.createdBetween(start, end));

        return orderRepository.findAll(specification, pageable)
                .map(this::mapWithUserInfo);
    }

    @Transactional
    public OrderResponseDTO updateOrder(Long id, OrderRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException(ORDER_DTO_NULL);
        }
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND + id));

        if (dto.getOrderItems() != null) {
            order.getOrderItems().clear();

            List<OrderItem> newItems = buildOrderItems(dto.getOrderItems(), order);
            order.getOrderItems().addAll(newItems);
            order.setPrice(calculateTotalPrice(newItems));
        }

        Order savedOrder = orderRepository.save(order);
        return mapWithUserInfo(savedOrder);
    }


    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND + id));

        order.setStatus(status);

        Order savedOrder = orderRepository.save(order);
        return mapWithUserInfo(savedOrder);
    }


    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND + id));
        orderRepository.delete(order);
    }

    private List<OrderItem> buildOrderItems(List<OrderItemRequestDTO> itemDTOs, Order order) {
        List<Long> itemIds = itemDTOs.stream()
                .map(OrderItemRequestDTO::getItemId)
                .toList();

        Map<Long, Item> itemsById = findItemsByIds(itemIds);

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequestDTO itemDto : itemDTOs) {
            Item item = itemsById.get(itemDto.getItemId());

            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setOrder(order);

            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private Map<Long, Item> findItemsByIds(List<Long> itemIds) {
        Map<Long, Item> itemsById = itemRepository.findAllById(itemIds).stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        for (Long id : itemIds) {
            if (!itemsById.containsKey(id)) {
                throw new NotFoundException(ITEM_NOT_FOUND + id);
            }
        }

        return itemsById;
    }

    private BigDecimal calculateTotalPrice(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(oi -> oi.getItem().getPrice().multiply(BigDecimal.valueOf(oi.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderResponseDTO mapWithUserInfo(Order order) {
        OrderResponseDTO dto = orderMapper.toResponseDto(order);
        dto.setUser(userServiceClient.getUserById(order.getUserId()));
        return dto;
    }
}
