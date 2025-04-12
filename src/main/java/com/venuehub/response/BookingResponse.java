package com.venuehub.response;


import com.venuehub.enums.BookingStatus;

public record BookingResponse(
        Long bookingId,
        BookingStatus status,
        String reservedBy,
        String venueName,
        Long venueId,
        String bookingDate,
        String reservationExpiry
) {
}
