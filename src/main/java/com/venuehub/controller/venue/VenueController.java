package com.venuehub.controller.venue;


import com.venuehub.enums.BookingStatus;
import com.venuehub.exceptions.UserForbiddenException;
import com.venuehub.dto.VenueDto;
import com.venuehub.entity.Booking;
import com.venuehub.entity.Venue;
import com.venuehub.response.VenueAddedResponse;
import com.venuehub.response.VenueListResponse;
import com.venuehub.service.VenueService;
import com.venuehub.utils.SecurityChecks;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
public class VenueController implements VenueApi {
    private final Logger logger = LoggerFactory.getLogger(VenueController.class);

    private final VenueService venueService;


    @Autowired
    public VenueController(VenueService venueService ) {
        this.venueService = venueService;
    }

    @GetMapping("/venue/{id}")
    public ResponseEntity<VenueDto> getVenueById(@PathVariable Long id) {
        logger.info("Received request to get venue with id: {}", id);

        VenueDto dto = venueService.getVenueDto(id);

        //Redundant - Just to make the test pass
//        if (venue == null) throw new NoSuchVenueException();

        logger.info("Returning venue details for id: {}", id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping(value = "/venue")
    public ResponseEntity<VenueAddedResponse> addVenue(@RequestBody @Valid VenueDto body, @AuthenticationPrincipal Jwt jwt) {
        logger.info("Received request to add a new venue by user: {}", jwt.getSubject());

        SecurityChecks.vendorCheck(jwt);

        Venue newVenue = venueService.buildVenue(body, jwt.getSubject());

        logger.info("Venue added with id: {}, by the user: {}", newVenue.getId(), jwt.getSubject());

        VenueAddedResponse response = new VenueAddedResponse("success", newVenue.getId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/venue/{id}")
    public ResponseEntity<VenueListResponse> deleteVenue(@PathVariable long id, @AuthenticationPrincipal Jwt jwt) {
        logger.info("Received request to delete venue with id: {} by user: {}", id, jwt.getSubject());

        SecurityChecks.vendorCheck(jwt);

        Venue venue = venueService.findById(id);

        List<Booking> bookings = venue.getBookings().stream().filter(booking -> booking.getStatus() != BookingStatus.FAILED).toList();

        //If user does not exist or user role does not contain vendor or Venue does not have any bookings
        if (!jwt.getSubject().equals(venue.getUsername()) || !bookings.isEmpty()) {
            logger.warn("User: {} is not authorized to delete venue id: {} or venue has active bookings", jwt.getSubject(), id);
            throw new UserForbiddenException();
        }

        venueService.delete(venue);
        logger.info("Venue with id: {} deleted by user: {}", id, jwt.getSubject());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/venue")
    public ResponseEntity<VenueListResponse> getVenue() {
        logger.info("Received request to get all venues");


        VenueListResponse response = venueService.getActiveVenues();

        logger.info("Returning list of all venues");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/venue/vendor/all-venue")
    public ResponseEntity<VenueListResponse> getVenueByVendorName(@AuthenticationPrincipal Jwt jwt) {

        logger.info("Received request to get all venues for the vendor: {}", jwt.getSubject());
        SecurityChecks.vendorCheck(jwt);

        VenueListResponse response = venueService.getUserVenues(jwt.getSubject());

        logger.info("Returning list of venues for vendor: {}", jwt.getSubject());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/venue/{id}")
    public ResponseEntity<HttpStatus> updateVenue(@PathVariable long id, @RequestBody VenueDto body, @AuthenticationPrincipal Jwt jwt) {
        logger.info("Received request to update venue with id: {} by user: {}", id, jwt.getSubject());
        SecurityChecks.vendorCheck(jwt);

        Venue venue = venueService.findById(id);

        if (!jwt.getSubject().equals(venue.getUsername())) {
            logger.warn("User: {} is not authorized to update venue id: {}", jwt.getSubject(), id);
            throw new UserForbiddenException();
        }
        venue.setName(body.name());
        venue.setDescription(body.description());
        venue.setCapacity(Integer.parseInt(body.capacity()));
        venue.setPhone(body.phone());
        venue.setBookingPrice(Integer.parseInt(body.estimate()));
        venue.setType(body.venueType());
        venue.setStatus(body.status());

        venueService.save(venue);
        logger.info("Venue with id: {} updated by user: {}", id, jwt.getSubject());


        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
