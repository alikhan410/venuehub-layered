package com.venuehub.repository;

import com.venuehub.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "SELECT * FROM booking WHERE venue_id = :id AND status != 'FAILED'", nativeQuery = true)
    List<Booking> findByVenue(@Param("id") long id);

    @Query(value = "SELECT * FROM booking WHERE venue_id = :id", nativeQuery = true)
    List<Booking> AllBookingsByVenue(@Param("id") long id);

    @Query(value = "SELECT * FROM booking WHERE reserved_by = :reservedBy", nativeQuery = true)
    List<Booking> findByReservedBy(@Param("reservedBy") String reservedBy);

    @Query(value = "SELECT * FROM booking WHERE id = :id AND status = 'COMPLETED'", nativeQuery = true)
    Optional<Booking> findCompletedBookingById(@Param("id") long id);

    Optional<Booking> findByBookingDate(String bookingDateTime);

}