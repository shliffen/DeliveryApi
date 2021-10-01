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

/**
 * Controller for receiving a non-formatted address lines and checking is it possible to make delivery to  address
 */
@RestController
public class AddressControler {

    @Autowired
    AddressRepository addressRepository;

    GoogleMapService googleMapService = new GoogleMapService();

    /**
     * resolves a single line address into a structured one, which should be resolved from Google Maps
     * @param addressDto - non-structured address line
     * @return ResponseEntity with status of formatting Address Line (can or cannot be resolved, not supported for
     * delivery)
     */
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
