package com.shliffen.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Structure for keeping data of each Timeslot: ID, Start and End time, First and Second Deliveries owners.
 * Delivery owners uses due to check that just two deliveries can be at the same Timeslot. If one of them is empty -
 * that shows us that we can book the Timeslot for another one delivery. AddressID - can be used for connecting to
 * another table (Addresses) in DB.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Timeslot {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "TimeslotId")
    private Long id;
    private LocalDate startTime;
    private LocalDate endTime;
    @JsonIgnore
    private String addressID;

    @JsonIgnore
    private String firstDeliveryOwner;
    @JsonIgnore
    private String secondDeliveryOwner;

}
