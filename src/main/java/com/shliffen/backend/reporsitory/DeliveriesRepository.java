package com.shliffen.backend.reporsitory;

import org.springframework.data.repository.CrudRepository;
import com.shliffen.backend.model.Delivery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveriesRepository extends CrudRepository<Delivery, String> {

    public Delivery findDeliveryById(String id);
    public List<Delivery> findAll();
}
