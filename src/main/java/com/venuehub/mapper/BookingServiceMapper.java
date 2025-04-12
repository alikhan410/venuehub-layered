package com.venuehub.mapper;

import com.venuehub.dto.BookingDateDto;
import com.venuehub.dto.BookingDto;
import com.venuehub.entity.Booking;
import com.venuehub.response.BookingResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface BookingServiceMapper {
    BookingDto bookingToBookingDto(Booking booking);

    List<BookingDto> bookingsToBookingDtoList(List<Booking> bookings);

    BookingDateDto bookingToBookingDateTimeDto(Booking booking);

    default BookingResponse bookingToBookingResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getStatus(),
                booking.getReservedBy(),
                booking.getVenue().getName(),
                booking.getVenue().getId(),
                booking.getBookingDate(),
                booking.getReservationExpiry()
        );
    }
}
