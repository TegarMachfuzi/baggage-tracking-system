package com.baggage.mapper;

import com.baggage.dto.request.PassengerReqDto;
import com.baggage.dto.response.PassengerResDto;
import com.baggage.model.PassengerEntity;

public class PassengerMapper {

    public static PassengerEntity toEntity(PassengerReqDto dto) {
        if (dto == null) return null;
        
        PassengerEntity entity = new PassengerEntity();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setBookingRef(dto.getBookingRef());
        entity.setFlightInfo(dto.getFlightInfo());
        return entity;
    }

    public static PassengerResDto toDto(PassengerEntity entity) {
        if (entity == null) return null;
        
        PassengerResDto dto = new PassengerResDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setBookingRef(entity.getBookingRef());
        dto.setFlightInfo(entity.getFlightInfo());
        return dto;
    }
}
