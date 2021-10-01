package com.shliffen.backend.reporsitory;

import com.shliffen.backend.model.BookingDeliveryData;
import com.shliffen.backend.model.Status;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository which contains data (application forms) for booking deliveries, and uses for recovery booking data after
 * some issues or troubles on a server or client side
 */
@Repository
public interface ApplicationsForBookingRepository extends CrudRepository<BookingDeliveryData,String> {

    List<BookingDeliveryData> findAllByStatus(Status status);
}
