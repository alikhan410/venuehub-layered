package com.venuehub.dto;


import com.venuehub.enums.OrderStatus;

public record OrderDto(Long orderId, String username, String clientSecret, int amount, Long bookingId,
                       OrderStatus status) {
}
