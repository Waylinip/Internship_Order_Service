package org.example.internship_order_service.security;

import lombok.RequiredArgsConstructor;
import org.example.internship_order_service.client.UserServiceClient;
import org.example.internship_order_service.repository.OrderRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("orderSecurity")
@RequiredArgsConstructor
public class OrderSecurity {

    private final OrderRepository orderRepository;
    private final UserServiceClient userServiceClient;

    public boolean isOwner(Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Long authUserId)) {
            return false;
        }

        return orderRepository.findById(orderId)
                .map(order -> {
                    var user = userServiceClient.getUserByAuthUserId(authUserId);

                    return user != null && order.getUserId().equals(user.getId());
                })
                .orElse(false);
    }
    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Long authUserId)) {
            return false;
        }

        var user = userServiceClient.getUserByAuthUserId(authUserId);

        return user != null && user.getId().equals(userId);
    }
}
