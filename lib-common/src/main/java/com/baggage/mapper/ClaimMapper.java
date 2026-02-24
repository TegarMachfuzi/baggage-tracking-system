package com.baggage.mapper;

import com.baggage.dto.response.ClaimResDto;
import com.baggage.model.ClaimEntity;

public class ClaimMapper {

    public static ClaimResDto toDto(ClaimEntity entity) {
        if (entity == null) return null;
        
        ClaimResDto dto = new ClaimResDto();
        dto.setId(entity.getId());
        dto.setBaggageId(entity.getBaggageId());
        dto.setPassengerId(entity.getPassengerId());
        dto.setClaimType(entity.getClaimType());
        dto.setStatus(entity.getStatus());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setResolvedAt(entity.getResolvedAt());
        return dto;
    }

    public static ClaimEntity toEntity(ClaimResDto dto) {
        if (dto == null) return null;
        
        ClaimEntity entity = new ClaimEntity();
        entity.setId(dto.getId());
        entity.setBaggageId(dto.getBaggageId());
        entity.setPassengerId(dto.getPassengerId());
        entity.setClaimType(dto.getClaimType());
        entity.setStatus(dto.getStatus());
        entity.setDescription(dto.getDescription());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setResolvedAt(dto.getResolvedAt());
        return entity;
    }
}
