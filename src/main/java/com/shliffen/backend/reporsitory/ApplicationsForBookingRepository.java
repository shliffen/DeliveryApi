package com.shliffen.backend.reporsitory;

import com.shliffen.backend.model.Status;
import com.shliffen.backend.model.dto.DeliveryDto;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ApplicationsForBookingRepository extends CrudRepository<DeliveryDto,String> {

    public List<DeliveryDto> findAllByStatus(Status status);
}
