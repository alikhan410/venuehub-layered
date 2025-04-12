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
public class RemoveBookingService extends QuartzJobBean {
    private final Logger LOGGER = LoggerFactory.getLogger(RemoveBookingService.class);
    private final BookingService bookedVenueService;
    @Autowired
    public RemoveBookingService(BookingService bookedVenueService ) {
        this.bookedVenueService = bookedVenueService;
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
        Booking booking = bookedVenueService.findById(bookingId);

        if (booking.getStatus() == BookingStatus.FAILED) {
            LOGGER.info("Booking Failed");
            return;
        }

        booking.setStatus(BookingStatus.COMPLETED);
        bookedVenueService.save(booking);

        LOGGER.info("Booking Completed");
    }
}