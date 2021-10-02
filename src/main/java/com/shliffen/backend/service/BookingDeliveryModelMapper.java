package com.shliffen.backend.service;

import com.shliffen.backend.model.BookingDeliveryData;
import com.shliffen.backend.model.dto.DeliveryDto;
import org.modelmapper.ModelMapper;

import java.util.Objects;


/**
 * Class realization of ModelMapper for mapping process from Dto to Entity there and back
 */
public class BookingDeliveryModelMapper {

    private final ModelMapper mapper;

    public BookingDeliveryModelMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public BookingDeliveryData toEntity(DeliveryDto dto) {
        return Objects.isNull(dto) ? null : mapper.map(dto, BookingDeliveryData.class);
    }

    public DeliveryDto toDto(BookingDeliveryData entity) {
        return Objects.isNull(entity) ? null : mapper.map(entity, DeliveryDto.class);
    }
}
