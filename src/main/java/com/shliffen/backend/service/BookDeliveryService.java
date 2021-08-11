package com.shliffen.backend.service;

import com.shliffen.backend.model.BookingDeliveryData;
import com.shliffen.backend.model.dto.DeliveryDto;
import com.shliffen.backend.reporsitory.AddressRepository;
import com.shliffen.backend.reporsitory.DeliveriesRepository;
import com.shliffen.backend.reporsitory.TimeSlotsRepository;
import com.shliffen.backend.service.tasks.BookingTimeslotTask;
import com.shliffen.backend.service.tasks.RecoveryTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@Service
public class BookDeliveryService {

    @Autowired
    private DeliveriesRepository deliveriesRepository;
    @Autowired
    private TimeSlotsRepository timeSlotsRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private ApplicationContext applicationContext;
    private BlockingQueue<DeliveryDto> deliveryDtosQueue = new LinkedBlockingDeque<>();
    private BlockingQueue<BookingDeliveryData> deliveryDtosQueue2 = new LinkedBlockingDeque<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(BookDeliveryService.class);

    @Async
    public void addTimeslotBookingToQueue(DeliveryDto deliveryDto) {
        LOGGER.info("Booking new timeslot for delivery to queue " + deliveryDto);
        deliveryDtosQueue.add(deliveryDto);
        deliveryDtosQueue2.add(bookingDeliveryData);
    }

    @Async
    public void addTimeslotsBookingToQueue(List<DeliveryDto> deliveryDtoList) {
        LOGGER.info("Adding new timeslots for delivery to queue " + deliveryDtoList);
        deliveryDtosQueue.addAll(deliveryDtoList);
    }

    @Lookup
    public BookingTimeslotTask createBookingTimeslotTask() {
        return null;
    }

    @Lookup
    public RecoveryTask createRecoveryTask() {
        return null;
    }


    @EventListener(ApplicationReadyEvent.class)
    public void initOrderServiceThreads() {
        LOGGER.info("Starting processing threads");
        startProcessing();
        executeRecovery();
    }
    /**
     * Create recovery task in a separate thread
     * and runs processing of timeslots
     */
    public void executeRecovery() {
        var recoveryThread = createRecoveryTask();
        taskExecutor.execute(recoveryThread);
    }

    /**
     * Create main processing task in a separate thread
     * Processes NEW/PENDING timeslots
     */
    public void startProcessing() {
        var bookingTimeslotTask = createBookingTimeslotTask();
        bookingTimeslotTask.setProcessingQueue(deliveryDtosQueue);
        taskExecutor.execute(bookingTimeslotTask);
    }


}
