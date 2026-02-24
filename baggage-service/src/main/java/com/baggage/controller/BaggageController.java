package com.baggage.controller;

import com.baggage.dto.request.BaggageReqDto;
import com.baggage.dto.response.BaggageResDto;
import com.baggage.dto.response.ResponseModel;
import com.baggage.service.BaggageServiceImpl;
import com.baggage.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/baggage")
public class BaggageController {

    @Autowired
    private BaggageServiceImpl service;

    @PostMapping
    public ResponseEntity<ResponseModel> create(@RequestBody BaggageReqDto request) {
        BaggageResDto result = service.create(request);
        return ResponseEntity.ok(ResponseUtil.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel> getById(@PathVariable UUID id) {
        BaggageResDto result = service.getById(id);
        return ResponseEntity.ok(ResponseUtil.success(result));
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ResponseModel> getByBarcode(@PathVariable String barcode) {
        BaggageResDto result = service.getByBarcode(barcode);
        return ResponseEntity.ok(ResponseUtil.success(result));
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<ResponseModel> getByPassengerId(@PathVariable UUID passengerId) {
        List<BaggageResDto> result = service.getByPassengerId(passengerId);
        return ResponseEntity.ok(ResponseUtil.success(result));
    }

    @GetMapping("/flight/{flightNumber}")
    public ResponseEntity<ResponseModel> getByFlightNumber(@PathVariable String flightNumber) {
        List<BaggageResDto> result = service.getByFlightNumber(flightNumber);
        return ResponseEntity.ok(ResponseUtil.success(result));
    }

    @GetMapping
    public ResponseEntity<ResponseModel> getAll() {
        List<BaggageResDto> result = service.getAll();
        return ResponseEntity.ok(ResponseUtil.success(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel> update(@PathVariable UUID id, @RequestBody BaggageReqDto request) {
        BaggageResDto result = service.update(id, request);
        return ResponseEntity.ok(ResponseUtil.success(result));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ResponseModel> updateStatus(@PathVariable UUID id, @RequestParam String status) {
        BaggageResDto result = service.updateStatus(id, status);
        return ResponseEntity.ok(ResponseUtil.success(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(ResponseUtil.success("Baggage deleted successfully"));
    }
}

