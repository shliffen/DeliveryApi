package com.shliffen.backend.api;

import com.shliffen.backend.model.Address;
import com.shliffen.backend.reporsitory.AddressRepository;
import com.shliffen.backend.service.GoogleMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * Responsible controller for getting available timeslots
 */
@RestController
public class TimeslotsController {

    @Autowired
    AddressRepository addressRepository;
    GoogleMapService mapService = new GoogleMapService();

    /**
     * Request all available timeslots for formatted address
     * @param formattedAddress Address for request available timeslots
     * @return ResponseEntity with body which exist or List of available timeslots or message that address doesn't exist
     */
    @PostMapping("/timeslots")
    public Object resolveTimeslots(@RequestBody String formattedAddress) {
        if (mapService.checkIsAddressExists(formattedAddress)){
            List<Address> addresses = addressRepository.findAll();
            return new ResponseEntity<>(addresses,HttpStatus.OK);
        } else return new ResponseEntity<>("The address doesn't exist", HttpStatus.NOT_FOUND);
    }


}
