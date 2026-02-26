package com.baggage.mapper;

import com.baggage.dto.request.TrackingReqDto;
import com.baggage.dto.response.TrackingResDto;
import com.baggage.model.TrackingEntity;

public class TrackingMapper {

    public static TrackingResDto toDto(TrackingEntity entity) {
        if (entity == null) return null;
        
        TrackingResDto dto = new TrackingResDto();
        dto.setId(entity.getId());
        dto.setBaggageId(entity.getBaggageId());
        dto.setLocation(entity.getLocation());
        dto.setStatus(entity.getStatus());
        dto.setTimestamp(entity.getTimestamp());
        dto.setRemarks(entity.getRemarks());
        return dto;
    }

    public static TrackingEntity toEntity(TrackingResDto dto) {
        if (dto == null) return null;
        
        TrackingEntity entity = new TrackingEntity();
        entity.setId(dto.getId());
        entity.setBaggageId(dto.getBaggageId());
        entity.setLocation(dto.getLocation());
        entity.setStatus(dto.getStatus());
        entity.setTimestamp(dto.getTimestamp());
        entity.setRemarks(dto.getRemarks());
        return entity;
    }

    public static TrackingEntity toEntity(TrackingReqDto dto) {
        if (dto == null) return null;
        
        TrackingEntity entity = new TrackingEntity();
        entity.setBaggageId(dto.getBaggageId());
        entity.setLocation(dto.getLocation());
        entity.setStatus(dto.getStatus());
        entity.setRemarks(dto.getRemarks());
        return entity;
    }
}
