package com.shliffen.backend.service;

import com.github.agogs.holidayapi.api.APIConsumer;
import com.github.agogs.holidayapi.api.impl.HolidayAPIConsumer;
import com.github.agogs.holidayapi.model.Holiday;
import com.github.agogs.holidayapi.model.HolidayAPIResponse;
import com.github.agogs.holidayapi.model.QueryParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class HolidayApiService {

    @Value("${holidayAPIKey}")
    private String key;
    @Value("${holidayAPIYear}")
    private String year;
    private List<LocalDate> holidays = getHolidaysPerYear();

    private List<LocalDate> getHolidaysPerYear() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        List<LocalDate> holidayDates = new ArrayList<>();
        APIConsumer consumer = new HolidayAPIConsumer("https://holidayapi.com/v1/holidays");
        //generate the query parameters
        QueryParams params = new QueryParams();
        params.key(key)
              .country(QueryParams.Country.ISRAEL)
              .year(Integer.parseInt(year))
              .format(QueryParams.Format.XML)
              .pretty(true);
        try {
            HolidayAPIResponse response = consumer.getHolidays(params);
            //check the status code of the API call
            int status = response.getStatus();
            if (status != 200) {
                return new ArrayList<>();
            } else {
                List<Holiday> holidays = response.getHolidays();
                for (Holiday holiday : holidays) {
                    String date = holiday.getDate();
                    LocalDate localDate = LocalDate.parse(date, formatter);
                    holidayDates.add(localDate);
                }
                return holidayDates;
            }
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public List<LocalDate> getHolidays() {
        return holidays;
    }
}
