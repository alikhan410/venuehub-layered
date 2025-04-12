package com.venuehub.entity;

import com.venuehub.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "booking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reservedBy;
    private String bookingDate;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;
    private int bookingFee;
    private String reservationExpiry = LocalDateTime.now(ZoneId.of("Asia/Karachi")).plusMinutes(50).toString();
    private String phone;
    private int guests;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
