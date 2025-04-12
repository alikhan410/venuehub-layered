package com.venuehub.response;


import com.venuehub.entity.BookingOrder;

import java.util.List;

public record BookingOrderListResponse(List<BookingOrder> bookingOrders) {
}
