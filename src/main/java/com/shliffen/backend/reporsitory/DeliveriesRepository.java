package com.shliffen.backend.reporsitory;

import com.shliffen.backend.model.Delivery;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Contain Deliveries (with their status of Order inside). PagingAndSortingRepository used due to the fact that there
 * can be several thousand deliveries and for the convenience of receiving and displaying data on all deliveries by
 * future front-end
 */
@Repository
public interface DeliveriesRepository extends PagingAndSortingRepository<Delivery, String> {

    Delivery findDeliveryById(String id);
    List<Delivery> findAll();
}
