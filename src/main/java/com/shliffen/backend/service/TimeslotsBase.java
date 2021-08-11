package com.shliffen.backend.service;

import com.shliffen.backend.model.Timeslot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * I prepared this class in case in the future the courier API does not want/will not enter, the list of available
 * timeslots for a week in advance - the methods of this class can prepare empty timeslots in advance for a week in
 * advance. I have not built in the methods yet, since there was nothing about it in the task.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TimeslotsBase {

    private HashMap<String, List<Timeslot>> currentTimeslots = new LinkedHashMap<>();

    public void setDefaultTimeslotsQuantity(int quantity) {
        String[] currentWeekDays = getDatesOfWeek(0, LocalDate.now().toString());
        String[] nextWeekDays = getDatesOfWeek(1, LocalDate.now().toString());
        for (int i = 0; i < currentWeekDays.length; i++) {
            newTimeslotsForDay(currentWeekDays[i]);
            newTimeslotsForDay(nextWeekDays[i]);
        }
    }

    /**
     * @param n      - parameter that's explain what week we need. -1 - previous, 0 - current, 1 - next week
     * @param myDate - current date from were we want to calculate
     * @return array of dates in String format
     */
    private String[] getDatesOfWeek(int n, String myDate) {
        String[] date = new String[7];
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.SUNDAY);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            Date time = simpleDateFormat.parse(myDate);
            cal.setTime(time);

            int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
            if (dayWeek == 1) {
                cal.add(Calendar.DAY_OF_MONTH, -1);
            }
            cal.setFirstDayOfWeek(Calendar.SUNDAY);
            int day = cal.get(Calendar.DAY_OF_WEEK);
            cal.add(Calendar.DATE, (cal.getFirstDayOfWeek() - day + 7 * n));
            date[0] = simpleDateFormat.format(cal.getTime());
            for (int i = 1; i < 7; i++) {
                cal.add(Calendar.DATE, 1);
                date[i] = simpleDateFormat.format(cal.getTime());
            }
            return date;
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date;
    }

    private void newTimeslotsForDay(String dayForTimeslots) {
        try {
            List<Timeslot> list = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            cal.setTime(simpleDateFormat.parse(dayForTimeslots));
            cal.add(Calendar.HOUR_OF_DAY, 8);
            cal.getTime();
            //
            for (int i = 0; i < 10; i++) {
                Timeslot timeslot = new Timeslot();
                timeslot.setStartTime(cal.getTime().toInstant()
                                         .atZone(ZoneId.systemDefault())
                                         .toLocalDate());
                cal.add(Calendar.HOUR_OF_DAY, 1);
                timeslot.setEndTime(cal.getTime().toInstant()
                                       .atZone(ZoneId.systemDefault())
                                       .toLocalDate());
            }
            currentTimeslots.put(dayForTimeslots, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
