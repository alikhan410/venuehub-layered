package com.venuehub.response;


import com.venuehub.dto.BookingDateDto;

import java.util.List;

public record BookingDateListResponse(List<BookingDateDto> bookingDateList) {
}
