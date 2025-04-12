package com.venuehub.controller.booking;

import com.venuehub.dto.BookingDateDto;
import com.venuehub.dto.BookingDto;
import com.venuehub.exceptions.BookingUnavailableException;
import com.venuehub.exceptions.NoSuchBookingException;
import com.venuehub.response.BookingDateListResponse;
import com.venuehub.response.BookingListResponse;
import com.venuehub.response.BookingStatusResponse;
import com.venuehub.response.GetBookingsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface BookingApi {

    @Operation(summary = "Adds a booking", description = "Adds a booking for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successful operation",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BookingDto.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Booking unavailable or invalid request"
            )
    })
    ResponseEntity<BookingDto> addBooking(@PathVariable Long venueId, @Valid @RequestBody BookingDto body, @AuthenticationPrincipal Jwt jwt) throws BookingUnavailableException;

    @Operation(summary = "Retrieves bookings by venue", description = "Retrieves all bookings for a given venue")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BookingDateListResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Venue not found"
            )
    })
    ResponseEntity<BookingDateListResponse> getBookingByVenue(@PathVariable Long venueId);

    @Operation(summary = "Retrieves booking status", description = "Retrieves the status of a specific booking")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BookingStatusResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Booking not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User not authorized to access booking"
            )
    })
    ResponseEntity<BookingStatusResponse> getBookingStatus(@PathVariable long bookingId, @AuthenticationPrincipal Jwt jwt)throws NoSuchBookingException;

    @Operation(summary = "Retrieves bookings by user", description = "Retrieves all bookings associated with the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GetBookingsResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found or no bookings found"
            )
    })
    ResponseEntity<BookingListResponse>  getBookingByUser(@AuthenticationPrincipal Jwt jwt)  throws NoSuchBookingException;

    @Operation(summary = "Retrieves bookings by vendor", description = "Retrieves all bookings associated with the authenticated vendor")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GetBookingsResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Vendor not found or no bookings found"
            )
    })
    ResponseEntity<BookingListResponse> getBookingByVendor(@AuthenticationPrincipal Jwt jwt)throws NoSuchBookingException;

    @Operation(summary = "Cancels a booking", description = "Cancels a specific booking by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successful operation"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Booking cannot be cancelled or invalid request"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User not authorized to cancel booking"
            )
    })
    ResponseEntity<HttpStatus> cancelBooking(@PathVariable long bookingId, @AuthenticationPrincipal Jwt jwt) throws Exception;

    @Operation(summary = "Updates booking date", description = "Updates the date of a specific booking by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successful operation"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Booking date cannot be updated or invalid request"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User not authorized to update booking date"
            )
    })
    ResponseEntity<HttpStatus> updateBookingDate(@PathVariable long bookingId, @RequestBody BookingDateDto body, @AuthenticationPrincipal Jwt jwt)  throws Exception;
}

