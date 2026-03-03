package com.baggage.claim.service;

import com.baggage.dto.request.ClaimReqDto;
import com.baggage.dto.response.ClaimResDto;

import java.util.List;
import java.util.UUID;

public interface ClaimService {
    
    ClaimResDto create(ClaimReqDto request);
    
    ClaimResDto getById(UUID id);
    
    List<ClaimResDto> getByBaggageId(UUID baggageId);
    
    List<ClaimResDto> getByPassengerId(UUID passengerId);
    
    List<ClaimResDto> getAll();
    
    ClaimResDto updateStatus(UUID id, String status);
    
    void delete(UUID id);
}
