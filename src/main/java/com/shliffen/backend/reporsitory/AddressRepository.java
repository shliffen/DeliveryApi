package com.shliffen.backend.reporsitory;

import com.shliffen.backend.model.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Used for containing all addresses with formatted string type fields (by GoogleMap Service)
 */
@Repository
public interface AddressRepository extends CrudRepository<Address, String> {

    List<Address> findAll();
}
