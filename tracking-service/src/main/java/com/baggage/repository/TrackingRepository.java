package com.baggage.repository;

import com.baggage.model.TrackingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrackingRepository extends JpaRepository<TrackingEntity, UUID> {
    List<TrackingEntity> findByBaggageIdOrderByTimestampDesc(UUID baggageId);
    Optional<TrackingEntity> findFirstByBaggageIdOrderByTimestampDesc(UUID baggageId);
}
