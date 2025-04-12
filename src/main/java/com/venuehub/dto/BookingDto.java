package com.venuehub.dto;

import com.venuehub.enums.BookingStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record BookingDto(
        Long id,
        String reservedBy,
        String reservationExpiry,
        @NotNull @NotBlank(message = "Phone can not be null or blank") String phone,
        BookingStatus status,
        @NotNull @NotBlank(message = "Date can not be null or empty") String bookingDate,
        @Min(value = 0, message = "Invalid value") int guests
) {}