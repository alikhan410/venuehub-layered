package com.venuehub.mapper;

import com.venuehub.dto.BookingDto;
import com.venuehub.dto.VenueDto;
import com.venuehub.entity.Booking;
import com.venuehub.entity.Venue;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface VenueServiceMapper {

    List<BookingDto> bookingsToDtoList(List<Booking> bookings);

    BookingDto bookingToDto(Booking booking);

    List<VenueDto> venuesToDtoList(List<Venue> venues);

    VenueDto venueToDto(Venue venue);

}
