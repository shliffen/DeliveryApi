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

    public DeliveriesController(BookingDeliveryModelMapper bookingDeliveryModelMapper) {
        this.bookingDeliveryModelMapper = bookingDeliveryModelMapper;
    }


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

    @GetMapping("/deliveries/daily")
    public Object retrieveTodaysDeliveries() {
        return new ResponseEntity<>(getDayDeliveries(LocalDate.now()), HttpStatus.OK);
    }

    @GetMapping("/deliveries/weekly")
    public Object resolveWeekDeliveries() {
        Date currentDate = convertToDateViaInstant(LocalDate.now());
        LocalDate[]arrayOfDatesInWeek = getDaysOfWeek(currentDate);
        List<Delivery>resultList = new ArrayList<>();
        for (LocalDate localDate : arrayOfDatesInWeek) {
            resultList.addAll(getDayDeliveries(localDate));        }
        return new ResponseEntity<>(resultList, HttpStatus.OK);
    }

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

    private LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private Date convertToDateViaInstant(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
                                                .atZone(ZoneId.systemDefault())
                                                .toInstant());
    }
}
