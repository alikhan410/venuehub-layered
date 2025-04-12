package com.venuehub.service;

import com.venuehub.entity.BookingOrder;
import com.venuehub.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void save(BookingOrder bookingOrder) {
        orderRepository.save(bookingOrder);
    }

    public Optional<BookingOrder> findById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public BookingOrder findByBooking(Long bookingId, String customerName) {
        return orderRepository.findByBooking(bookingId, customerName);
    }

    public Optional<BookingOrder> findByClientSecret(String clientSecret) {
        return orderRepository.findByClientSecret(clientSecret);
    }

    public List<BookingOrder> findByUsername(String username) {
        return orderRepository.findByCustomerName(username);
    }

    public List<BookingOrder> findByServiceProvider(String serviceProvider) {
        return orderRepository.findByServiceProvider(serviceProvider);
    }
}
