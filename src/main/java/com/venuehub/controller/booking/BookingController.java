package com.venuehub.controller.booking;

import com.venuehub.dto.BookingDateDto;
import com.venuehub.dto.BookingDto;
import com.venuehub.entity.Booking;
import com.venuehub.entity.Venue;
import com.venuehub.enums.BookingStatus;
import com.venuehub.exceptions.BookingUnavailableException;
import com.venuehub.exceptions.NoSuchBookingException;
import com.venuehub.exceptions.UserForbiddenException;
import com.venuehub.mapper.BookingServiceMapper;
import com.venuehub.response.BookingDateListResponse;
import com.venuehub.response.BookingListResponse;
import com.venuehub.response.BookingStatusResponse;
import com.venuehub.service.BookingService;
import com.venuehub.service.JobService;
import com.venuehub.service.VenueService;
import com.venuehub.utils.SecurityChecks;
import jakarta.validation.Valid;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@Validated
public class BookingController implements BookingApi {
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;
    private final VenueService venueService;
    private final JobService jobService;
    private final BookingServiceMapper mapper = Mappers.getMapper(BookingServiceMapper.class);

    @Autowired
    public BookingController(BookingService bookingService, VenueService venueService, JobService jobService) {
        this.venueService = venueService;
        this.bookingService = bookingService;
        this.jobService = jobService;
    }

    @PostMapping("/bookings/venue/{venueId}")
    @Transactional
    public ResponseEntity<BookingDto> addBooking(@PathVariable Long venueId, @Valid @RequestBody BookingDto body, @AuthenticationPrincipal Jwt jwt) throws BookingUnavailableException {
        logger.info("Received request to add a booking for venueId: {} by user: {}", venueId, jwt.getSubject());

        SecurityChecks.userCheck(jwt);

        Venue venue = venueService.findById(venueId);

        LocalDate bookingDate = LocalDate.parse(body.bookingDate());

        //LocalDate today = LocalDate.parse("2019-03-27")
        if (!bookingService.isBookingAvailable(body.bookingDate())) {
            logger.warn("Booking is not available for date: {}", bookingDate);
            throw new BookingUnavailableException("Booking is not available for date: " + bookingDate);
        }

        BookingDto bookingDto = bookingService.addNewBooking(body, venue, jwt.getSubject());
        logger.info("Booking added with id: {} for the venueId: {}", bookingDto.id(), venueId);

        jobService.createNewBooking(bookingDto.id(), bookingDto.bookingDate(), bookingDto.reservationExpiry());

        return new ResponseEntity<>(bookingDto, HttpStatus.CREATED);
    }

    @GetMapping("/bookings/venue/{venueId}")
    public ResponseEntity<BookingDateListResponse> getBookingByVenue(@PathVariable Long venueId) {
        logger.info("Received request to get bookings for venueId: {}", venueId);

        venueService.findById(venueId);

        List<BookingDateDto> dates = bookingService.bookingDatesByVenue(venueId);
        BookingDateListResponse response = new BookingDateListResponse(dates);

        logger.info("Returning bookings for venueId: {}", venueId);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<BookingStatusResponse> getBookingStatus(@PathVariable long bookingId, @AuthenticationPrincipal Jwt jwt) throws NoSuchBookingException {
        logger.info("Received request to get booking status for bookingId: {} by user: {}", bookingId, jwt.getSubject());

        SecurityChecks.userCheck(jwt);

        Booking booking = bookingService.findById(bookingId);

        if (!booking.getReservedBy().equals(jwt.getSubject())) {
            logger.warn("User: {} is not authorized to access booking id: {}", jwt.getSubject(), bookingId);
            throw new UserForbiddenException();
        }

        BookingStatusResponse response = new BookingStatusResponse(
                booking.getBookingDate(),
                booking.getGuests(),
                booking.getStatus(),
                booking.getVenue().getName(),
                booking.getVenue().getId()
        );

        logger.info("Returning booking status for bookingId: {}", bookingId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/bookings/user")
    public ResponseEntity<BookingListResponse> getBookingByUser(@AuthenticationPrincipal Jwt jwt) throws NoSuchBookingException {
        logger.info("Received request to get bookings by user: {}", jwt.getSubject());

        SecurityChecks.userCheck(jwt);

        List<Booking> bookingList = bookingService.findByReservedBy(jwt.getSubject());
        BookingListResponse bookingsList = new BookingListResponse(bookingList.stream().map(mapper::bookingToBookingResponse).toList());

        logger.info("Returning bookings for user: {}", jwt.getSubject());
        return new ResponseEntity<>(bookingsList, HttpStatus.OK);
    }

    @GetMapping("/bookings/vendor")
    public ResponseEntity<BookingListResponse> getBookingByVendor(@AuthenticationPrincipal Jwt jwt) throws NoSuchBookingException {
        logger.info("Received request to get bookings by vendor: {}", jwt.getSubject());

        SecurityChecks.vendorCheck(jwt);

        List<Venue> venues = venueService.findByUsername(jwt.getSubject());

        List<Booking> bookings = venues
                .stream()
                .flatMap(venue -> venue.getBookings().stream())
                .toList();

        BookingListResponse bookingsList = new BookingListResponse(bookings.stream().map(mapper::bookingToBookingResponse).toList());

        logger.info("Returning bookings for vendor: {}", jwt.getSubject());
        return new ResponseEntity<>(bookingsList, HttpStatus.OK);
    }

    @DeleteMapping("/bookings/{bookingId}")
    @Transactional
    public ResponseEntity<HttpStatus> cancelBooking(@PathVariable long bookingId, @AuthenticationPrincipal Jwt jwt) throws Exception {
        logger.info("Received request to cancel booking with id: {} by user: {}", bookingId, jwt.getSubject());
        SecurityChecks.userCheck(jwt);

        Booking booking = bookingService.findById(bookingId);

        if (!booking.getReservedBy().equals(jwt.getSubject())) {
            logger.warn("User: {} is not authorized to cancel booking id: {}", jwt.getSubject(), bookingId);
            throw new UserForbiddenException();
        }

        if (!booking.getStatus().equals(BookingStatus.BOOKED)) {
            logger.error("Booking id: {} is not in BOOKED status, cannot cancel", bookingId);
            //TODO Add a new exception
            throw new Exception("Not booked");
        }

        //updating the booking as failed
        booking.setStatus(BookingStatus.FAILED);
        bookingService.save(booking);

        logger.info("Booking with id: {} cancelled by user: {}", bookingId, jwt.getSubject());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/bookings/{bookingId}")
    @Transactional
    public ResponseEntity<HttpStatus> updateBookingDate(@PathVariable long bookingId, @RequestBody BookingDateDto body, @AuthenticationPrincipal Jwt jwt) throws Exception {
        logger.info("Received request to update booking date for bookingId: {} by user: {}", bookingId, jwt.getSubject());

        SecurityChecks.userCheck(jwt);

        Booking booking = bookingService.findById(bookingId);

        if (!booking.getReservedBy().equals(jwt.getSubject())) {
            logger.warn("User: {} is not authorized to update booking id: {}", jwt.getSubject(), bookingId);
            throw new UserForbiddenException();
        }

        if (!booking.getStatus().equals(BookingStatus.BOOKED)) {
            logger.error("Booking id: {} is not in BOOKED status, cannot update date", bookingId);
            //TODO Add a new exception
            throw new Exception("Not booked");
        }

        //updating both the booking and reservation
        bookingService.updateBooking(booking, body.bookingDate());

        jobService.cancelBookingJob(String.valueOf(bookingId));
        jobService.cancelReservationJob(String.valueOf(bookingId));

        jobService.createNewBooking(booking.getId(), booking.getBookingDate(), booking.getReservationExpiry());

        logger.info("Booking date for bookingId: {} updated by user: {}", bookingId, jwt.getSubject());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
