package com.shliffen.backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Structure which contains information about delivery. Unique ID, current Status of Delivery, current Timeslot, and
 * Delivery Owner's name.
 */
@Setter
@Getter
@Entity
@NoArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Status status = Status.NEW;
    @OneToOne
    @JoinColumn(name = "TimeslotId", nullable = false)
    private Timeslot timeslot;
    private String deliveryOwner;
}
