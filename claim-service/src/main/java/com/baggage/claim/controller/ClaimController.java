package com.baggage.claim.controller;

import com.baggage.claim.service.ClaimService;
import com.baggage.dto.request.ClaimReqDto;
import com.baggage.dto.response.ClaimResDto;
import com.baggage.dto.response.ResponseModel;
import com.baggage.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/claim")
public class ClaimController {
    
    @Autowired
    private ClaimService service;
    
    @PostMapping
    public ResponseEntity<ResponseModel> create(@RequestBody ClaimReqDto request) {
        ClaimResDto result = service.create(request);
        return ResponseEntity.ok(ResponseUtil.success(result));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel> getById(@PathVariable UUID id) {
        ClaimResDto result = service.getById(id);
        return ResponseEntity.ok(ResponseUtil.success(result));
    }
    
    @GetMapping("/baggage/{baggageId}")
    public ResponseEntity<ResponseModel> getByBaggageId(@PathVariable UUID baggageId) {
        List<ClaimResDto> result = service.getByBaggageId(baggageId);
        return ResponseEntity.ok(ResponseUtil.success(result));
    }
    
    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<ResponseModel> getByPassengerId(@PathVariable UUID passengerId) {
        List<ClaimResDto> result = service.getByPassengerId(passengerId);
        return ResponseEntity.ok(ResponseUtil.success(result));
    }
    
    @GetMapping
    public ResponseEntity<ResponseModel> getAll() {
        List<ClaimResDto> result = service.getAll();
        return ResponseEntity.ok(ResponseUtil.success(result));
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<ResponseModel> updateStatus(
            @PathVariable UUID id,
            @RequestParam String status) {
        ClaimResDto result = service.updateStatus(id, status);
        return ResponseEntity.ok(ResponseUtil.success(result));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(ResponseUtil.success(null));
    }
}
