package com.shliffen.backend.api;

import com.shliffen.backend.model.BookingDeliveryData;
import com.shliffen.backend.model.Delivery;
import com.shliffen.backend.model.Status;
import com.shliffen.backend.model.Timeslot;
import com.shliffen.backend.model.dto.DeliveryDto;
import com.shliffen.backend.reporsitory.DeliveriesRepository;
import com.shliffen.backend.reporsitory.TimeSlotsRepository;
import com.shliffen.backend.service.BookDeliveryService;
import com.shliffen.backend.service.BookingDeliveryModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;

/**
 * Controller responsible for request of data about deliveries
 */
@RestController
public class DeliveriesController {

    private SimpleDateFormat simpleFormat = new SimpleDateFormat("dd-MM-yyyy");
    @Autowired
    private DeliveriesRepository deliveriesRepository;
    @Autowired
    private TimeSlotsRepository timeSlotsRepository;
    @Autowired
    BookDeliveryService bookDeliveryService;
    private final BookingDeliveryModelMapper bookingDeliveryModelMapper;

    /**
     * Initialization of Mapper for transition from DTO to Entity
     * @param bookingDeliveryModelMapper instance of BookingDeliveryModelMapper
     */
    public DeliveriesController(BookingDeliveryModelMapper bookingDeliveryModelMapper) {
        this.bookingDeliveryModelMapper = bookingDeliveryModelMapper;
    }

    /**
     * Trying to book a delivery to desirable Timeslot
     * @param deliveryDto Data from user (in JSON format) with Username and timeslotID
     * @return ResponseEntity with Status of operation - OK or Issue Message
     */
    @PostMapping("/deliveries")
    public Object BookDelivery(@Valid @RequestBody DeliveryDto deliveryDto) {
        try {
            BookingDeliveryData bookingDeliveryData =  bookingDeliveryModelMapper.toEntity(deliveryDto);
            bookDeliveryService.addTimeslotBookingToQueue(bookingDeliveryData);
            return new ResponseEntity<>("Timeslot booked successfully",HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("Sorry, you can't book delivery for this day", HttpStatus.CONFLICT);
        }
    }

    /**
     * Mark delivery with id DELIVERY_ID as Completed
     * @param DELIVERY_ID - id of desirable delivery, which we want to mark as complete
     * @return ResponseEntity with Status of operation - OK or Issue Message
     */
    @PostMapping("/deliveries/{DELIVERY_ID}/complete")
    public Object MarkDeliveryAsCompleted(@PathVariable String DELIVERY_ID) {
        Delivery delivery = deliveriesRepository.findById(DELIVERY_ID).orElse(null);
        if (delivery != null) {
            delivery.setStatus(Status.COMPLETE);
            deliveriesRepository.save(delivery);
            return new ResponseEntity<>(HttpStatus.OK);
        } else return new ResponseEntity<>("Sorry, there is no delivery with this ID in the database.",
                                           HttpStatus.NOT_FOUND);
    }

    /**
     * Cancellation of delivery by ID
     * @param DELIVERY_ID - id of desirable delivery, which we want to cancel
     * @return ResponseEntity with Status of operation - OK or Issue Message
     */
    @DeleteMapping("/deliveries/{DELIVERY_ID}")
    public Object cancellationDelivery(@PathVariable String DELIVERY_ID) {
        if (deliveriesRepository.existsById(DELIVERY_ID)) {
            Delivery delivery = deliveriesRepository.findDeliveryById(DELIVERY_ID);
            Timeslot timeslot = timeSlotsRepository.findTimeslotById(delivery.getTimeslot().getId());
            if (delivery.getDeliveryOwner().equals(timeslot.getFirstDeliveryOwner())){
                timeslot.setFirstDeliveryOwner("");
            } else {
                timeslot.setSecondDeliveryOwner("");
            }
            deliveriesRepository.deleteById(DELIVERY_ID);
            return new ResponseEntity<>(HttpStatus.OK);
        } else return new ResponseEntity<>("Sorry, there is no delivery with this ID in the database.",
                                           HttpStatus.NOT_FOUND);
    }

    /**
     * Request for getting today's deliveries
     * @return ResponseEntity with List of Deliveries in body
     */
    @GetMapping("/deliveries/daily")
    public Object retrieveTodaysDeliveries() {
        return new ResponseEntity<>(getDayDeliveries(LocalDate.now()), HttpStatus.OK);
    }

    /**
     * Request all deliveries for current week
     * @return ResponseEntity with List of Deliveries in body
     */
    @GetMapping("/deliveries/weekly")
    public Object resolveWeekDeliveries() {
        Date currentDate = convertToDateViaInstant(LocalDate.now());
        LocalDate[]arrayOfDatesInWeek = getDaysOfWeek(currentDate);
        List<Delivery>resultList = new ArrayList<>();
        for (LocalDate localDate : arrayOfDatesInWeek) {
            resultList.addAll(getDayDeliveries(localDate));        }
        return new ResponseEntity<>(resultList, HttpStatus.OK);
    }

    /**
     * Get list of all deliveries for desirable date
     * @param date desirable date for getting deliveries
     * @return List of Delivery objects for desirable date
     */
    private List<Delivery>getDayDeliveries(LocalDate date){
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

    /**
     * Getting all Days (in LocalDate format) of the current|desirable week
     * @param refDate desirable date, inside the week. Due to API used in Israel we start the week from Sunday
     * @return array of LocalDate objects
     */
    private LocalDate[] getDaysOfWeek(Date refDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(refDate);
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        LocalDate[] daysOfWeek = new LocalDate[7];
        for (int i = 0; i < 7; i++) {
            daysOfWeek[i] = convertToLocalDateViaInstant(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return daysOfWeek;
    }

    /**
     * Method for transform Date object to LocalDate format object
     * @param dateToConvert Date object to convert
     * @return LocalDate object as a result of conversion
     */
    private LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Method for transform LocalDate object to Date format object
     * @param dateToConvert LocalDate object to convert
     * @return Date object as a result of conversion
     */
    private Date convertToDateViaInstant(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
                                                .atZone(ZoneId.systemDefault())
                                                .toInstant());
    }
}
