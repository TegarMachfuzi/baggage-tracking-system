package com.baggage.controller;

import com.baggage.dto.request.TrackingReqDto;
import com.baggage.dto.response.ResponseModel;
import com.baggage.dto.response.TrackingResDto;
import com.baggage.service.TrackingServiceImpl;
import com.baggage.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import com.baggage.constant.TrackingStatus;
import java.util.Map;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    @Autowired
    private TrackingServiceImpl service;

    private static final Map<String, String> STATUS_DEFAULT_LOCATIONS = Map.of(
        TrackingStatus.CHECKED_IN.name(),          "Check-in Counter",
        TrackingStatus.SECURITY_CHECK.name(),       "Security Screening Area",
        TrackingStatus.LOADING_TO_AIRCRAFT.name(),  "Baggage Handling / Loading Bay",
        TrackingStatus.IN_TRANSIT.name(),           "Aircraft Hold",
        TrackingStatus.ARRIVED.name(),              "Arrival Baggage Area",
        TrackingStatus.CUSTOMS_CHECK.name(),        "Customs & Immigration",
        TrackingStatus.READY_FOR_PICKUP.name(),     "Baggage Claim Belt"
    );

    @GetMapping("/locations")
    public ResponseEntity<ResponseModel> getDefaultLocations() {
        return ResponseEntity.ok(ResponseUtil.success(STATUS_DEFAULT_LOCATIONS));
    }

    @PostMapping
    public ResponseEntity<ResponseModel> create(@RequestBody TrackingReqDto request) {
        TrackingResDto result = service.create(request);
        return ResponseEntity.ok(ResponseUtil.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel> getById(@PathVariable UUID id) {
        TrackingResDto result = service.getById(id);
        return ResponseEntity.ok(ResponseUtil.success(result));
    }

    @GetMapping("/baggage/{baggageId}")
    public ResponseEntity<ResponseModel> getByBaggageId(@PathVariable UUID baggageId) {
        List<TrackingResDto> result = service.getByBaggageId(baggageId);
        return ResponseEntity.ok(ResponseUtil.success(result));
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ResponseModel> getByBarcode(@PathVariable String barcode) {
        List<TrackingResDto> result = service.getByBarcode(barcode);
        return ResponseEntity.ok(ResponseUtil.success(result));
    }

    @GetMapping("/latest/{baggageId}")
    public ResponseEntity<ResponseModel> getLatest(@PathVariable UUID baggageId) {
        TrackingResDto result = service.getLatestByBaggageId(baggageId);
        return ResponseEntity.ok(ResponseUtil.success(result));
    }

    @GetMapping("/latest/barcode/{barcode}")
    public ResponseEntity<ResponseModel> getLatestByBarcode(@PathVariable String barcode) {
        TrackingResDto result = service.getLatestByBarcode(barcode);
        return ResponseEntity.ok(ResponseUtil.success(result));
    }
}
