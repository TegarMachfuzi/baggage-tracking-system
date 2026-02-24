package com.baggage.mapper;

import com.baggage.dto.request.BaggageReqDto;
import com.baggage.dto.response.BaggageResDto;
import com.baggage.model.BaggageEntity;

public class BaggageMapper {

    public static BaggageEntity toEntity(BaggageReqDto dto) {
        if (dto == null) return null;
        
        BaggageEntity entity = new BaggageEntity();
        entity.setId(dto.getBaggageId());
        entity.setBarcode(dto.getBarcode());
        entity.setPassengerId(dto.getPassengerId());
        entity.setFlightNumber(dto.getFlightNumber());
        entity.setOrigin(dto.getOrigin());
        entity.setDestination(dto.getDestination());
        entity.setStatus(dto.getStatus());
        return entity;
    }

    public static BaggageResDto toDto(BaggageEntity entity) {
        if (entity == null) return null;
        
        BaggageResDto dto = new BaggageResDto();
        dto.setId(entity.getId());
        dto.setBarcode(entity.getBarcode());
        dto.setPassengerId(entity.getPassengerId());
        dto.setFlightNumber(entity.getFlightNumber());
        dto.setOrigin(entity.getOrigin());
        dto.setDestination(entity.getDestination());
        dto.setStatus(entity.getStatus());
        dto.setLastUpdated(entity.getLastUpdated());
        return dto;
    }
}
