package com.venuehub.response;

import com.venuehub.dto.BookingDto;

import java.util.List;

public record BookingsResponse(List<BookingDto> bookings) {
}
