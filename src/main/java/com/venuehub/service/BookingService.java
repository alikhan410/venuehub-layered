package com.venuehub.service;

import com.venuehub.dto.BookingDateDto;
import com.venuehub.dto.BookingDto;
import com.venuehub.entity.Booking;
import com.venuehub.entity.Venue;
import com.venuehub.enums.BookingStatus;
import com.venuehub.exceptions.NoSuchBookingException;
import com.venuehub.mapper.BookingServiceMapper;
import com.venuehub.repository.BookingRepository;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    public static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final BookingRepository bookingRepository;
    private final BookingServiceMapper mapper = Mappers.getMapper(BookingServiceMapper.class);

    @Autowired
    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    @CacheEvict(value = {"booking:id", "booking:complete", "bookings:venueId", "bookings:username", "bookings:datetime"},allEntries = true)
    public void save(Booking booking) {
        bookingRepository.save(booking);
    }

    public List<Booking> findByVenue(Long venueId) {
        return bookingRepository.findByVenue(venueId);
    }

    @Cacheable(value = "bookings:venueId", key = "#venueId")
    public List<BookingDto> loadByVenue(Long venueId) {
        List<Booking> bookings = bookingRepository.findByVenue(venueId);
        return mapper.bookingsToBookingDtoList(bookings);
    }

    public Booking findById(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> {
            logger.error("Booking not found with id: {}", id);
            return new NoSuchBookingException();
        });

        return booking;
    }

    public BookingDto loadById(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(NoSuchBookingException::new);
        return mapper.bookingToBookingDto(booking);
    }

    public List<Booking> findByReservedBy(String reservedBy) {
        return bookingRepository.findByReservedBy(reservedBy);
    }

    public List<BookingDto> loadByUsername(String username) {
        List<Booking> bookings = bookingRepository.findByReservedBy(username);
        return mapper.bookingsToBookingDtoList(bookings);
    }


    public Optional<Booking> findCompletedBookingById(long id) {
        return bookingRepository.findCompletedBookingById(id);
    }

    public BookingDto loadCompletedBookingById(long id) {
        Booking booking = bookingRepository.findCompletedBookingById(id).orElseThrow(NoSuchBookingException::new);
        return mapper.bookingToBookingDto(booking);
    }

    public List<BookingDateDto> bookingDatesByVenue(long id) {
        List<Booking> bookings = bookingRepository.findByVenue(id);
        return bookings.stream().map(mapper::bookingToBookingDateTimeDto).toList();
    }

    public Boolean isBookingAvailable(String bookingDate) {
        LocalDate localBookingDate = LocalDate.parse(bookingDate);
        LocalDate currentDate = LocalDate.now();

        if (bookingRepository.findByBookingDate(bookingDate).isPresent()) return false;
        if (!localBookingDate.isAfter(currentDate)) return false;

        return true;
    }

    public List<Booking> removeBooking(List<Booking> bookings, long bookingId) {
        ArrayList<Booking> updatedBookings = new ArrayList<>(bookings);

        Iterator<Booking> iterator = updatedBookings.iterator();

        while (iterator.hasNext()) {
            Booking booking = iterator.next();
            if (booking.getId() == bookingId) {
                iterator.remove();
                return updatedBookings;
            }
        }
        return updatedBookings;
    }


    @Transactional
    public void deleteBooking(long id) {
        bookingRepository.deleteById(id);
    }

    @Transactional
    public void updateStatus(long id, BookingStatus status) throws NoSuchBookingException {
        Booking booking = bookingRepository.findById(id).orElseThrow(NoSuchBookingException::new);
        booking.setStatus(status);
        bookingRepository.save(booking);
    }

    @Transactional
    public BookingDto addNewBooking(BookingDto body, Venue venue, String username) {
        Booking newBooking = Booking.builder()
                .bookingDate(body.bookingDate())
                .status(BookingStatus.RESERVED)
                .venue(venue)
                .bookingFee(venue.getBookingPrice())
                .reservationExpiry(LocalDateTime.now(ZoneId.of("Asia/Karachi")).plusMinutes(50).toString())
                .reservedBy(username)
                .phone(body.phone())
                .guests(body.guests()).build();
        bookingRepository.save(newBooking);

        return mapper.bookingToBookingDto(newBooking);
    }

    @Transactional
    public void updateBooking(Booking booking, String newBookingDate) {
        booking.setBookingDate(newBookingDate);

        //setting a new reservation date
        String newReservation = LocalDateTime.now(ZoneId.of("PLT", ZoneId.SHORT_IDS)).plusMinutes(2).toString();
        booking.setReservationExpiry(newReservation);

        bookingRepository.save(booking);
    }

}
