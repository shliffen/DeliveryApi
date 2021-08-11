package com.shliffen.backend.service.tasks;

import com.shliffen.backend.model.Delivery;
import com.shliffen.backend.model.Status;
import com.shliffen.backend.model.Timeslot;
import com.shliffen.backend.model.dto.DeliveryDto;
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
    private BlockingQueue<DeliveryDto> processingQueue;
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingTimeslotTask.class);


    @Override
    public void run() {
        Thread.currentThread().setName("TimeslotBookingProcessingThread");
        LOGGER.info("Starting processing thread");
        while (!Thread.currentThread().isInterrupted()) {
            DeliveryDto deliveryDtoProcess = null;
            Delivery deliveryForBooking = new Delivery();
            try {
                deliveryDtoProcess = processingQueue.take();
                LOGGER.info("Started processing new booking ");
                boolean isValidTimeslot = checkTimeslotExist(deliveryDtoProcess);
                if (!isValidTimeslot) {
                    LOGGER.info("Unknown TimeslotId=" + deliveryDtoProcess.getTimeslotID());
                }
                Timeslot timeslot =
                        timeSlotsRepository.findTimeslotById(Long.parseLong(deliveryDtoProcess.getTimeslotID()));
                var canBePlaced = checkTimeslotExist(deliveryDtoProcess)&&(checkBookingPossibility(timeslot));
                if (canBePlaced){
                    if (timeslot.getFirstDeliveryOwner().isEmpty()) {
                        timeslot.setFirstDeliveryOwner(deliveryDtoProcess.getUser());
                        timeslotAndDeliveryBooking(timeslot,deliveryDtoProcess,deliveryForBooking);
                    } else if (timeslot.getSecondDeliveryOwner().isEmpty()) {
                        timeslot.setSecondDeliveryOwner(deliveryDtoProcess.getUser());
                        timeslotAndDeliveryBooking(timeslot,deliveryDtoProcess,deliveryForBooking);
                    }
                }
            } catch (NoSuchElementException e){
                LOGGER.error(e.getMessage());
                LOGGER.info("Timeslot booking REJECTED!" + deliveryDtoProcess);
                throw new NoSuchElementException();
            } catch (InterruptedException e) {
                LOGGER.error("Processing interrupted", e);
                Thread.currentThread().interrupt();
            }


        }

    }

    private boolean checkTimeslotExist(DeliveryDto deliveryDto) {
        Timeslot timeslot = timeSlotsRepository.findTimeslotById(Long.parseLong(deliveryDto.getTimeslotID()));
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

    public BlockingQueue<DeliveryDto> getProcessingQueue() {
        return processingQueue;
    }

    public void setProcessingQueue(
            BlockingQueue<DeliveryDto> processingQueue) {
        this.processingQueue = processingQueue;
    }

    private void timeslotAndDeliveryBooking(Timeslot timeslot, DeliveryDto deliveryDtoProcess, Delivery deliveryForBooking){
        deliveryForBooking.setTimeslot(timeslot);
        deliveryForBooking.setStatus(Status.ORDERED);
        deliveryForBooking.setDeliveryOwner(deliveryDtoProcess.getUser());
        deliveriesRepository.save(deliveryForBooking);
        LOGGER.info("Finished booking timeslot for delivery" + deliveryForBooking);
        deliveryDtoProcess.setStatus(Status.ORDERED);
    }

}
