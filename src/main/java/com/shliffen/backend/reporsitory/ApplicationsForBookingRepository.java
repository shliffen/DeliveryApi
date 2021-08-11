package com.shliffen.backend.reporsitory;

import com.shliffen.backend.model.BookingDeliveryData;
import com.shliffen.backend.model.Status;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ApplicationsForBookingRepository extends CrudRepository<BookingDeliveryData,String> {

    public List<BookingDeliveryData> findAllByStatus(Status status);
}
