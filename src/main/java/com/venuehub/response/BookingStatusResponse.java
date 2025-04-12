package com.venuehub.response;


import com.venuehub.enums.BookingStatus;

public record BookingStatusResponse(
        String bookingDate,
        int guests,
        BookingStatus status,
        String venueName,
        Long venueId
) {
}
