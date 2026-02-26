package com.passenger.service;

import com.passenger.dto.PassengerReqDto;
import com.passenger.entity.Passenger;
import com.passenger.exception.DuplicatePassengerException;
import com.passenger.exception.PassengerNotFoundException;
import com.passenger.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PassengerServiceImpl implements PassengerService {
    
    @Autowired
    private PassengerRepository repository;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Override
    @Transactional
    @CacheEvict(value = "passengers", allEntries = true)
    public Passenger create(PassengerReqDto dto) {
        if (repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicatePassengerException("Email already exists");
        }
        
        if (repository.findByPassportNumber(dto.getPassportNumber()).isPresent()) {
            throw new DuplicatePassengerException("Passport number already exists");
        }
        
        Passenger passenger = new Passenger();
        passenger.setName(dto.getName());
        passenger.setEmail(dto.getEmail());
        passenger.setPhone(dto.getPhone());
        passenger.setPassportNumber(dto.getPassportNumber());
        passenger.setNationality(dto.getNationality());
        
        Passenger saved = repository.save(passenger);
        kafkaTemplate.send("passenger-events", "created", saved);
        return saved;
    }
    
    @Override
    @Cacheable(value = "passengers", key = "#id")
    public Passenger getById(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new PassengerNotFoundException("Passenger not found with id: " + id));
    }
    
    @Override
    @Cacheable(value = "passengers")
    public List<Passenger> getAll() {
        return repository.findAll();
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "passengers", allEntries = true)
    public Passenger update(String id, PassengerReqDto dto) {
        Passenger passenger = getById(id);
        
        repository.findByEmail(dto.getEmail()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicatePassengerException("Email already exists");
            }
        });
        
        repository.findByPassportNumber(dto.getPassportNumber()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicatePassengerException("Passport number already exists");
            }
        });
        
        passenger.setName(dto.getName());
        passenger.setEmail(dto.getEmail());
        passenger.setPhone(dto.getPhone());
        passenger.setPassportNumber(dto.getPassportNumber());
        passenger.setNationality(dto.getNationality());
        
        Passenger updated = repository.save(passenger);
        kafkaTemplate.send("passenger-events", "updated", updated);
        return updated;
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "passengers", allEntries = true)
    public void delete(String id) {
        Passenger passenger = getById(id);
        repository.delete(passenger);
        kafkaTemplate.send("passenger-events", "deleted", passenger);
    }
}
