package com.shliffen.backend.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.GeocodingResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleMapService {

    @Value("${googleGeocodeAPIKey}")
    private String apiKey;

    private final GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(apiKey)
            .build();


    public String getFormattedAddress(String unformattedAddress){
        try {
            GeocodingResult[] results = GeocodingApi.geocode(context,
                                                             unformattedAddress).await();
            String formattedAddress = results[0].formattedAddress;
            String city;
            if (formattedAddress.contains("Tel-Aviv")) return formattedAddress;
            else return "*" + formattedAddress;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            context.shutdown();
        }
        return "";
    }

    public boolean checkIsAddressExists(String formattedAddress){
        try {
            GeocodingResult[] results = GeocodingApi.geocode(context,
                                                             formattedAddress).await();
            AddressComponent[]components = results[0].addressComponents;
            if (components.length>=4) return true;
            else return false;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            context.shutdown();
        }
        return false;
    }

}
