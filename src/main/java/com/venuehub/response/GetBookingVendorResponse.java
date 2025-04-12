package com.venuehub.response;

import com.venuehub.enums.BookingStatus;

public record GetBookingVendorResponse(
        Long bookingId,
        BookingStatus status,
        String username,
        String venueName,
        Long venueId,
        String bookingDate,
        String reservationExpiry
) {
}

