package com.shliffen.backend.model.dto;

import lombok.*;

/**
 * Structure for getting from user non-formatted address for checking existing, capability of delivery, and transform
 * by Google Maps Service to formatted in 'Address' structure
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddressDto {

    private String searchTerm;
}
