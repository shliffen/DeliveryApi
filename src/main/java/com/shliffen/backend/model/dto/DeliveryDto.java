package com.shliffen.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shliffen.backend.model.Status;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Structure used for getting data from user for booking delivery in DeliveriesController
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryDto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @JsonProperty("user")
    private String user;
    @JsonProperty("timeslotID")
    private String timeslotID;
    @JsonIgnore
    private Status status = Status.NEW;
}
