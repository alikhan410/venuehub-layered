package com.venuehub.service;

import com.venuehub.entity.Booking;
import com.venuehub.enums.BookingStatus;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Service
public class RemoveReservationService extends QuartzJobBean {
    private final Logger LOGGER = LoggerFactory.getLogger(RemoveReservationService.class);
    private final BookingService bookingService;
    @Autowired
    public RemoveReservationService(BookingService bookedVenueService ) {
        this.bookingService = bookedVenueService;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();

        long bookingId = (long) jobDataMap.get("bookingId");

        try {
            removeBooking(bookingId);
        } catch (Exception e) {
            throw new JobExecutionException();
        }
    }

    public void removeBooking(long bookingId) {

        Booking booking = bookingService.findById(bookingId);
        if (booking.getStatus() == BookingStatus.BOOKED) {
            return;
        }
        booking.setStatus(BookingStatus.FAILED);
        bookingService.save(booking);

        LOGGER.info("Booking reservation removed, customer unable to pay");

    }
}
