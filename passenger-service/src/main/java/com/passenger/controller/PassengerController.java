package com.passenger.controller;

import com.baggage.dto.response.ResponseModel;
import com.baggage.util.ResponseUtil;
import com.passenger.dto.PassengerReqDto;
import com.passenger.entity.Passenger;
import com.passenger.service.PassengerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {
    
    @Autowired
    private PassengerService service;
    
    @PostMapping
    public ResponseEntity<ResponseModel> create(@Valid @RequestBody PassengerReqDto dto) {
        Passenger passenger = service.create(dto);
        return ResponseEntity.ok(ResponseUtil.success(passenger));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel> getById(@PathVariable String id) {
        Passenger passenger = service.getById(id);
        return ResponseEntity.ok(ResponseUtil.success(passenger));
    }
    
    @GetMapping
    public ResponseEntity<ResponseModel> getAll() {
        List<Passenger> passengers = service.getAll();
        return ResponseEntity.ok(ResponseUtil.success(passengers));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel> update(@PathVariable String id, @Valid @RequestBody PassengerReqDto dto) {
        Passenger passenger = service.update(id, dto);
        return ResponseEntity.ok(ResponseUtil.success(passenger));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok(ResponseUtil.success("Passenger deleted successfully"));
    }
}
