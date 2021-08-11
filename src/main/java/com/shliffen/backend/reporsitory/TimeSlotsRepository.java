package com.shliffen.backend.reporsitory;

import com.shliffen.backend.model.Timeslot;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeSlotsRepository extends CrudRepository<Timeslot, Long> {

    public Timeslot findTimeslotById(Long id);
    public List<Timeslot> findAll();

}
