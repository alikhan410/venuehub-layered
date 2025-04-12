package com.venuehub.response;

import java.util.List;


public record BookingListResponse(List<BookingResponse> bookings) {
}
//@Data
//public class BookingListResponse {
//    private final List<BookingDto> bookingList;
//
//    public BookingListResponse(List<BookingDto> bookingList){
//        this.bookingList = bookingList;
//    }
//}