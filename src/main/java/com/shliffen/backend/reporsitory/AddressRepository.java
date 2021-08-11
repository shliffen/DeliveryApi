package com.shliffen.backend.reporsitory;

import com.shliffen.backend.model.Address;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AddressRepository extends CrudRepository<Address, String> {

    public List<Address> findAll();
}
