package com.shliffen.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shliffen.backend.model.Timeslot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Structure for getting available timeslots for the next week in CourierAPI
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourierApiObject {

    @JsonProperty("timeslotsForNextWeek")
    List<Timeslot> timeslotsForNextWeek;
}
