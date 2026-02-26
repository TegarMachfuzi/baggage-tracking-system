package com.passenger.service;

import com.passenger.dto.PassengerReqDto;
import com.passenger.entity.Passenger;
import java.util.List;

public interface PassengerService {
    Passenger create(PassengerReqDto dto);
    Passenger getById(String id);
    List<Passenger> getAll();
    Passenger update(String id, PassengerReqDto dto);
    void delete(String id);
}
