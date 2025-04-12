package com.venuehub.entity;

import com.venuehub.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "booking_order")
@NoArgsConstructor
public class BookingOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String customerName;

    private String serviceProvider;

    private String clientSecret;

    private int amount;

    private Long bookingId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public BookingOrder( String customerName, String serviceProvider, String clientSecret, int amount, Long bookingId ) {
        this.customerName = customerName;
        this.serviceProvider = serviceProvider;
        this.clientSecret = clientSecret;
        this.amount = amount;
        this.bookingId = bookingId;
        this.orderStatus = OrderStatus.PENDING;
    }
}
