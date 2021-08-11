package com.shliffen.backend.service.tasks;

import com.shliffen.backend.model.BookingDeliveryData;
import com.shliffen.backend.model.Delivery;
import com.shliffen.backend.model.Status;
import com.shliffen.backend.model.Timeslot;
import com.shliffen.backend.reporsitory.DeliveriesRepository;
import com.shliffen.backend.reporsitory.TimeSlotsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;

@Component
@Scope("prototype")
public class BookingTimeslotTask implements Runnable  {

    private SimpleDateFormat simpleFormat = new SimpleDateFormat("dd-MM-yyyy");
    @Autowired
    TimeSlotsRepository timeSlotsRepository;
    @Autowired
    DeliveriesRepository deliveriesRepository;
    private BlockingQueue<BookingDeliveryData> processingQueue;
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingTimeslotTask.class);

    @Override
    public void run() {
        Thread.currentThread().setName("TimeslotBookingProcessingThread");
        LOGGER.info("Starting processing thread");
        while (!Thread.currentThread().isInterrupted()) {
            BookingDeliveryData bookingDeliveryData = null;
            Delivery deliveryForBooking = new Delivery();
            try {
                bookingDeliveryData = processingQueue.take();
                LOGGER.info("Started processing new booking ");
                boolean isValidTimeslot = checkTimeslotExist(bookingDeliveryData);
                if (!isValidTimeslot) {
                    LOGGER.info("Unknown TimeslotId=" + bookingDeliveryData.getTimeslotID());
                }
                Timeslot timeslot =
                        timeSlotsRepository.findTimeslotById(Long.parseLong(bookingDeliveryData.getTimeslotID()));
                var canBePlaced = checkTimeslotExist(bookingDeliveryData)&&(checkBookingPossibility(timeslot));
                if (canBePlaced){
                    if (timeslot.getFirstDeliveryOwner().isEmpty()) {
                        timeslot.setFirstDeliveryOwner(bookingDeliveryData.getUser());
                        timeslotAndDeliveryBooking(timeslot,bookingDeliveryData,deliveryForBooking);
                    } else if (timeslot.getSecondDeliveryOwner().isEmpty()) {
                        timeslot.setSecondDeliveryOwner(bookingDeliveryData.getUser());
                        timeslotAndDeliveryBooking(timeslot,bookingDeliveryData,deliveryForBooking);
                    }
                }
            } catch (NoSuchElementException e){
                LOGGER.error(e.getMessage());
                LOGGER.info("Timeslot booking REJECTED!" + bookingDeliveryData);
                throw new NoSuchElementException();
            } catch (InterruptedException e) {
                LOGGER.error("Processing interrupted", e);
                Thread.currentThread().interrupt();
            }


        }

    }

    private boolean checkTimeslotExist(BookingDeliveryData bookingDeliveryData) {
        Timeslot timeslot = timeSlotsRepository.findTimeslotById(Long.parseLong(bookingDeliveryData.getTimeslotID()));
        return timeslot != null;
    }

    private boolean checkBookingPossibility(Timeslot timeslot){
        boolean emptyFieldOwner = false;
        if ((timeslot.getFirstDeliveryOwner().isEmpty()) || (timeslot.getSecondDeliveryOwner().isEmpty())){
            emptyFieldOwner = true;
        }
        int quantityOfDeliveriesOfSameWithTimeSlotDay =
                getDayDeliveries(timeslot.getStartTime()).size();
        return (emptyFieldOwner) && (quantityOfDeliveriesOfSameWithTimeSlotDay < 10);
    }

    private List<Delivery> getDayDeliveries(LocalDate date){
        List<Delivery> resultList = new ArrayList<>();
        List<Delivery> allDeliveries = deliveriesRepository.findAll();
        for (Delivery delivery : allDeliveries) {
            if ((simpleFormat.format(delivery.getTimeslot().getStartTime()).equals(simpleFormat.format(date))) &&
                (simpleFormat.format(delivery.getTimeslot().getEndTime()).equals(simpleFormat.format(date)))) {
                resultList.add(delivery);
            }
        }
        return resultList;
    }

    public BlockingQueue<BookingDeliveryData> getProcessingQueue() {
        return processingQueue;
    }

    public void setProcessingQueue(
            BlockingQueue<BookingDeliveryData> processingQueue) {
        this.processingQueue = processingQueue;
    }

    private void timeslotAndDeliveryBooking(Timeslot timeslot, BookingDeliveryData bookingDeliveryData, Delivery deliveryForBooking){
        deliveryForBooking.setTimeslot(timeslot);
        deliveryForBooking.setStatus(Status.ORDERED);
        deliveryForBooking.setDeliveryOwner(bookingDeliveryData.getUser());
        deliveriesRepository.save(deliveryForBooking);
        LOGGER.info("Finished booking timeslot for delivery" + deliveryForBooking);
        bookingDeliveryData.setStatus(Status.ORDERED);
    }

}
