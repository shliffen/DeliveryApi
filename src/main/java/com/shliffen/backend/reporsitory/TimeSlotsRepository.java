package com.shliffen.backend.reporsitory;

import com.shliffen.backend.model.Timeslot;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository of all available for deliveries Time Slots. It can be replenished using a specially prepared json file
 * (transmitted via Courier API) or automatically (10 timeslots per day) using TimeslotsBase Service (you can connect
 * ready-made methods, but now they not yet used)
 */
@Repository
public interface TimeSlotsRepository extends CrudRepository<Timeslot, Long> {

    Timeslot findTimeslotById(Long id);
    List<Timeslot> findAll();

}
