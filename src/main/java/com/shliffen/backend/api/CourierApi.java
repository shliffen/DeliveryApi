package com.shliffen.backend.api;

import com.shliffen.backend.model.Timeslot;
import com.shliffen.backend.model.dto.CourierApiObject;
import com.shliffen.backend.reporsitory.TimeSlotsRepository;
import com.shliffen.backend.service.HolidayApiService;
import com.shliffen.backend.service.LocalDateComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class CourierApi {

    @Autowired
    TimeSlotsRepository timeSlotsRepository;
    @Autowired
    HolidayApiService holidayApiService;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private SimpleDateFormat simpleFormat = new SimpleDateFormat("dd-MM-yyyy");


    @PostMapping("/courier/set/timeslots")
    public Object setAvailableTimeslots(@RequestBody CourierApiObject courierApiObject) {
        List<LocalDate> holidays = holidayApiService.getHolidays();
        List<Timeslot> timeslotsForNextWeek = courierApiObject.getTimeslotsForNextWeek();
        List<Timeslot> timeslotsFallOnHolidays = new ArrayList<>();
        for (Timeslot timeslot : timeslotsForNextWeek){
            String timeslotStartTime = simpleFormat.format(timeslot.getStartTime());
            LocalDate date = LocalDate.parse(timeslotStartTime, formatter);
            //checking that timeslot does not fall on a holiday
            Collections.sort(holidays);
            int index = Collections.binarySearch(holidays, date, new LocalDateComparator());
            if (index<0) {
                timeSlotsRepository.save(timeslot);
            } else {
                timeslotsFallOnHolidays.add(timeslot);
            }
        }
        if (timeslotsFallOnHolidays.size()>0) {
            return new ResponseEntity<>("Timeslots for the upcoming week were saved", HttpStatus.OK);
        } else {
            //if there are some timeslots that falls on holidays
            return new ResponseEntity<>(timeslotsFallOnHolidays, HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
