package com.venuehub.repository;


import com.venuehub.entity.BookingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<BookingOrder, Long> {

    @Query(value = "SELECT * FROM booking_order WHERE booking_id = :id and customer_name = :customerName", nativeQuery = true)
    BookingOrder findByBooking(@Param("id") Long bookingId, @Param("customerName") String customerName);

    @Query(value = "SELECT * FROM booking_order WHERE client_secret = :clientSecret" , nativeQuery = true)
    Optional<BookingOrder> findByClientSecret(@Param("clientSecret") String clientSecret);

    List<BookingOrder> findByCustomerName(String customerName);
    List<BookingOrder> findByServiceProvider(String serviceProvider);
}
