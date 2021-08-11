package com.shliffen.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
//@Table(name = "timeslot")
public class Timeslot {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "timeslot_id")
    private Long id;
    private LocalDate startTime;
    private LocalDate endTime;
    //@JsonIgnore
    //private String dayOfTheWeek = new SimpleDateFormat("EEEE").format(startTime);
    @JsonIgnore
    private String addressID;

    @JsonIgnore
    private String firstDeliveryOwner;
    @JsonIgnore
    private String secondDeliveryOwner;

}
