package com.shliffen.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Structure for Booking Deliveries by Clients. It is an intermediate structure used, among other things,
 * to avoid conflicts when using multithreading.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BookingDeliveryData {

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
