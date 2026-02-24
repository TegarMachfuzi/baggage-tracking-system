package com.baggage.repository;

import com.baggage.model.BaggageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BaggageRepository extends JpaRepository<BaggageEntity, UUID> {
    Optional<BaggageEntity> findByBarcode(String barcode);
    List<BaggageEntity> findByPassengerId(UUID passengerId);
    List<BaggageEntity> findByStatus(String status);
    List<BaggageEntity> findByFlightNumber(String flightNumber);
}
