package com.shliffen.backend.api;

import com.shliffen.backend.model.Address;
import com.shliffen.backend.model.dto.AddressDto;
import com.shliffen.backend.reporsitory.AddressRepository;
import com.shliffen.backend.service.GoogleMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AddressControler {

    @Autowired
    AddressRepository addressRepository;

    GoogleMapService googleMapService = new GoogleMapService();

    @PostMapping("/resolve-address")
    public Object resolveAddress(@RequestBody AddressDto addressDto) {
            String formattedAddress = googleMapService.getFormattedAddress(addressDto.getSearchTerm());
            if (formattedAddress.isEmpty()) {
                return new ResponseEntity<>("Address cannot be resolved", HttpStatus.NOT_ACCEPTABLE);
            } else if (formattedAddress.startsWith("*")) {
                return new ResponseEntity<>("Address is not supported for delivery", HttpStatus.NOT_ACCEPTABLE);
            } else {
                Address address = new Address();
                address.setFormattedAddress(formattedAddress);
                addressRepository.save(address);
            }
        return new ResponseEntity<>("The address was resolved successfully", HttpStatus.OK);
    }
}
