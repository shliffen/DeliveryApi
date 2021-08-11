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

@RestController
public class TimeslotsController {

    @Autowired
    AddressRepository addressRepository;
    GoogleMapService mapService = new GoogleMapService();

    @PostMapping("/timeslots")
    public Object resolveTimeslots(@RequestBody String formattedAddress) {
        if (mapService.checkIsAddressExists(formattedAddress)){
            List<Address> addresses = addressRepository.findAll();
            return new ResponseEntity<>(addresses,HttpStatus.OK);
        } else return new ResponseEntity<>("The address doesn't exist", HttpStatus.NOT_FOUND);
    }


}
