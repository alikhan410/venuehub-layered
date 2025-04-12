package com.venuehub.service;

import com.venuehub.dto.VenueDto;
import com.venuehub.entity.Booking;
import com.venuehub.entity.Image;
import com.venuehub.entity.Venue;
import com.venuehub.exceptions.NoSuchVenueException;
import com.venuehub.mapper.VenueServiceMapper;
import com.venuehub.repository.VenueRepository;
import com.venuehub.response.VenueListResponse;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VenueService {
    private final Logger logger = LoggerFactory.getLogger(VenueService.class);

    private final VenueRepository venueRepository;
    private final VenueServiceMapper venueServiceMapper = Mappers.getMapper(VenueServiceMapper.class);

    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    @Transactional
    public void save(Venue venue) {
        venueRepository.save(venue);
    }

    public Venue findById(Long id) {
        return venueRepository.findById(id).orElseThrow(() -> {
            logger.error("Venue not found with id: {}", id);
            return new NoSuchVenueException();
        });

    }

    public List<Venue> findByUsername(String username) {
        return venueRepository.findByUsername(username);
    }

    public List<Venue> findAllActiveVenues() {
        return venueRepository.findAllActiveVenues();
    }

    public VenueDto getVenueDto(Venue venue) {
        return venueServiceMapper.venueToDto(venue);
    }

    public VenueDto getVenueDto(Long id) {
        Venue venue = venueRepository.findById(id).orElseThrow(NoSuchVenueException::new);
        return venueServiceMapper.venueToDto(venue);
    }

    public VenueListResponse getActiveVenues() {
        List<Venue> venues = findAllActiveVenues();
        List<VenueDto> dtoList = venueServiceMapper.venuesToDtoList(venues);
        return new VenueListResponse(dtoList);
    }

    public VenueListResponse getUserVenues(String username) {
        List<Venue> venues = findByUsername(username);
        List<VenueDto> dtoList = venueServiceMapper.venuesToDtoList(venues);
        return new VenueListResponse(dtoList);
    }

    public List<VenueDto> getVenueDtoList(List<Venue> venues) {
        return venueServiceMapper.venuesToDtoList(venues);
    }

    @Transactional
    public void delete(Venue venue) {
        venueRepository.delete(venue);
    }

    public Venue buildVenue(VenueDto body, String username) {
        Set<Booking> bookings = new HashSet<>();

        // Convert URLs to Image entities
        List<Image> images = body.images().stream()
                .map(image -> new Image(image.getUrl()))
                .toList();

        Venue venue = Venue.builder()
                .type(body.venueType())
                .username(username)
                .images(images)
                .phone(body.phone())
                .name(body.name())
                .description(body.description())
                .location(body.location())
                .bookingPrice(Integer.parseInt(body.estimate()))
                .bookings(bookings)
                .capacity(Integer.parseInt(body.capacity()))
                .status(body.status())
                .build();
        return venueRepository.save(venue);
    }
}
