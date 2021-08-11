package com.shliffen.backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
//@Table(name = "delivery")
@Entity
@NoArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Status status = Status.NEW;
    @OneToOne
    @JoinColumn(name = "timeslot_id", nullable = false)
    private Timeslot timeslot;

    private String deliveryOwner;

}
