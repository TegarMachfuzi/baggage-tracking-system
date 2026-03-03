package com.baggage.claim.service;

import com.baggage.constant.ClaimStatus;
import com.baggage.dto.event.ClaimEventDto;
import com.baggage.dto.request.ClaimReqDto;
import com.baggage.dto.response.ClaimResDto;
import com.baggage.mapper.ClaimMapper;
import com.baggage.model.ClaimEntity;
import com.baggage.claim.repository.ClaimRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ClaimServiceImpl implements ClaimService {
    
    private static final Logger log = LoggerFactory.getLogger(ClaimServiceImpl.class);
    
    @Autowired
    private ClaimRepository repository;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public ClaimResDto create(ClaimReqDto request) {
        log.info("Creating claim for baggageId: {}", request.getBaggageId());
        
        ClaimEntity entity = ClaimMapper.toEntity(request);
        entity.setStatus(ClaimStatus.SUBMITTED.name());
        entity.setCreatedAt(LocalDateTime.now());
        entity = repository.save(entity);
        
        // Cache
        String cacheKey = "claim:" + entity.getId();
        redisTemplate.opsForValue().set(cacheKey, entity, 1, TimeUnit.HOURS);
        
        // Publish event
        ClaimEventDto event = new ClaimEventDto();
        event.setClaimId(entity.getId());
        event.setBaggageId(entity.getBaggageId());
        event.setPassengerId(entity.getPassengerId());
        event.setClaimType(entity.getClaimType());
        event.setStatus(entity.getStatus());
        event.setDescription(entity.getDescription());
        event.setTimestamp(LocalDateTime.now());
        
        kafkaTemplate.send("claim-created", event);
        log.info("Published claim-created event for claimId: {}", entity.getId());
        
        return ClaimMapper.toDto(entity);
    }
    
    @Override
    public ClaimResDto getById(UUID id) {
        log.info("Getting claim by id: {}", id);
        
        // Try cache first
        String cacheKey = "claim:" + id;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("Cache hit for claim: {}", id);
            return ClaimMapper.toDto((ClaimEntity) cached);
        }
        
        // Get from database
        ClaimEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found: " + id));
        
        // Cache it
        redisTemplate.opsForValue().set(cacheKey, entity, 1, TimeUnit.HOURS);
        
        return ClaimMapper.toDto(entity);
    }
    
    @Override
    public List<ClaimResDto> getByBaggageId(UUID baggageId) {
        log.info("Getting claims by baggageId: {}", baggageId);
        
        return repository.findByBaggageId(baggageId).stream()
                .map(ClaimMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ClaimResDto> getByPassengerId(UUID passengerId) {
        log.info("Getting claims by passengerId: {}", passengerId);
        
        return repository.findByPassengerId(passengerId).stream()
                .map(ClaimMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ClaimResDto> getAll() {
        log.info("Getting all claims");
        
        return repository.findAll().stream()
                .map(ClaimMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public ClaimResDto updateStatus(UUID id, String status) {
        log.info("Updating claim status: {} to {}", id, status);
        
        ClaimEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found: " + id));
        
        entity.setStatus(status);
        
        if (ClaimStatus.RESOLVED.name().equals(status)) {
            entity.setResolvedAt(LocalDateTime.now());
        }
        
        entity = repository.save(entity);
        
        // Invalidate cache
        String cacheKey = "claim:" + id;
        redisTemplate.delete(cacheKey);
        
        return ClaimMapper.toDto(entity);
    }
    
    @Override
    public void delete(UUID id) {
        log.info("Deleting claim: {}", id);
        
        repository.deleteById(id);
        
        // Invalidate cache
        String cacheKey = "claim:" + id;
        redisTemplate.delete(cacheKey);
    }
}
