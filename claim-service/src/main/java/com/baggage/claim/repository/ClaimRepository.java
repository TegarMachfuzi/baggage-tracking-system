package com.baggage.claim.repository;

import com.baggage.model.ClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClaimRepository extends JpaRepository<ClaimEntity, UUID> {
    
    List<ClaimEntity> findByBaggageId(UUID baggageId);
    
    List<ClaimEntity> findByPassengerId(UUID passengerId);
    
    List<ClaimEntity> findByStatus(String status);
}
