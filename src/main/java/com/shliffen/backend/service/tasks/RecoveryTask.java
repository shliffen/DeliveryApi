package com.shliffen.backend.service.tasks;

import com.shliffen.backend.model.BookingDeliveryData;
import com.shliffen.backend.model.Status;
import com.shliffen.backend.reporsitory.ApplicationsForBookingRepository;
import com.shliffen.backend.service.BookDeliveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
public class RecoveryTask implements Runnable {

    @Autowired
    private ApplicationsForBookingRepository applicationsForBookingRepository;
    @Autowired
    private BookDeliveryService bookDeliveryService;
    private static final Logger LOGGER = LoggerFactory.getLogger(RecoveryTask.class);

    @Override
    public void run() {
        Thread.currentThread().setName("TimeslotBookingRecoveryThread");
        while(!Thread.currentThread().isInterrupted()) {
            LOGGER.info("Starting Timeslots booking recovery from database");
            List<BookingDeliveryData> notProcessedTimeslotBookings = findNotProcessedDeliveries();
            if (notProcessedTimeslotBookings != null && !notProcessedTimeslotBookings.isEmpty()) {
                LOGGER.info("Found total " + notProcessedTimeslotBookings.size() + " unprocessed bookings");
                LOGGER.info("Adding not processed bookings to processing queue...");
                bookDeliveryService.addTimeslotsBookingToQueue(notProcessedTimeslotBookings);
            }
            Thread.currentThread().interrupt();
        }
        LOGGER.info("Recovery finished");
    }

    private List<BookingDeliveryData> findNotProcessedDeliveries() {
        return applicationsForBookingRepository.findAllByStatus(Status.NEW);
    }
}
