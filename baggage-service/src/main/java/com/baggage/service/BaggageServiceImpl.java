package com.baggage.service;

import com.baggage.config.KafkaTopicConfig;
import com.baggage.constant.BaggageStatus;
import com.baggage.dto.event.BaggageEventDto;
import com.baggage.dto.request.BaggageReqDto;
import com.baggage.dto.response.BaggageResDto;
import com.baggage.mapper.BaggageMapper;
import com.baggage.model.BaggageEntity;
import com.baggage.repository.BaggageRepository;
import com.baggage.util.BarcodeGenerator;
import com.baggage.util.ValidationUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class BaggageServiceImpl {

    @Autowired
    private BaggageRepository repository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String CACHE_KEY_PREFIX = "baggage:";
    private static final String CACHE_KEY_BARCODE_PREFIX = "baggage:barcode:";
    private static final long CACHE_TTL_HOURS = 24;

    public BaggageResDto create(BaggageReqDto request) {
        // Validate
        if (!ValidationUtil.isValidUUID(request.getPassengerId().toString())) {
            throw new IllegalArgumentException("Invalid passenger ID");
        }
        if (ValidationUtil.isEmpty(request.getFlightNumber())) {
            throw new IllegalArgumentException("Flight number is required");
        }

        // Generate barcode
        String barcode = BarcodeGenerator.generate();

        // Map & Save
        BaggageEntity entity = BaggageMapper.toEntity(request);
        entity.setBarcode(barcode);
        entity.setStatus(BaggageStatus.CHECKED_IN.name());
        entity = repository.save(entity);

        // Cache the result
        BaggageResDto dto = BaggageMapper.toDto(entity);
        cacheData(entity.getId(), barcode, dto);

        // Publish event
        publishBaggageEvent(entity, "CREATED");

        return dto;
    }

    public BaggageResDto getById(UUID id) {
        // Try cache first
        String cacheKey = CACHE_KEY_PREFIX + id;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        
        if (cached != null) {
            // Convert from LinkedHashMap to BaggageResDto
            return objectMapper.convertValue(cached, BaggageResDto.class);
        }

        // Cache MISS - query database
        BaggageEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Baggage not found"));
        
        BaggageResDto dto = BaggageMapper.toDto(entity);
        
        // Store in cache
        cacheData(id, entity.getBarcode(), dto);
        
        return dto;
    }

    public BaggageResDto getByBarcode(String barcode) {
        // Try cache first
        String cacheKey = CACHE_KEY_BARCODE_PREFIX + barcode;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        
        if (cached != null) {
            // Convert from LinkedHashMap to BaggageResDto
            return objectMapper.convertValue(cached, BaggageResDto.class);
        }

        // Cache MISS - query database
        BaggageEntity entity = repository.findByBarcode(barcode)
                .orElseThrow(() -> new RuntimeException("Baggage not found"));
        
        BaggageResDto dto = BaggageMapper.toDto(entity);
        
        // Store in cache
        cacheData(entity.getId(), barcode, dto);
        
        return dto;
    }

    public List<BaggageResDto> getByPassengerId(UUID passengerId) {
        return repository.findByPassengerId(passengerId).stream()
                .map(BaggageMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BaggageResDto> getByFlightNumber(String flightNumber) {
        return repository.findByFlightNumber(flightNumber).stream()
                .map(BaggageMapper::toDto)
                .collect(Collectors.toList());
    }

    public BaggageResDto updateStatus(UUID id, String status) {
        BaggageEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Baggage not found"));
        
        entity.setStatus(status);
        entity = repository.save(entity);

        // Update cache
        BaggageResDto dto = BaggageMapper.toDto(entity);
        cacheData(id, entity.getBarcode(), dto);

        // Publish event
        publishBaggageEvent(entity, "UPDATED");

        return dto;
    }

    public BaggageResDto update(UUID id, BaggageReqDto request) {
        BaggageEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Baggage not found"));

        entity.setFlightNumber(request.getFlightNumber());
        entity.setOrigin(request.getOrigin());
        entity.setDestination(request.getDestination());
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        entity = repository.save(entity);

        // Update cache
        BaggageResDto dto = BaggageMapper.toDto(entity);
        cacheData(id, entity.getBarcode(), dto);

        // Publish event
        publishBaggageEvent(entity, "UPDATED");

        return dto;
    }

    public void delete(UUID id) {
        BaggageEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Baggage not found"));
        
        // Delete from database
        repository.deleteById(id);
        
        // Invalidate cache
        invalidateCache(id, entity.getBarcode());
    }

    public List<BaggageResDto> getAll() {
        return repository.findAll().stream()
                .map(BaggageMapper::toDto)
                .collect(Collectors.toList());
    }

    private void publishBaggageEvent(BaggageEntity entity, String eventType) {
        try {
            BaggageEventDto event = new BaggageEventDto();
            event.setBaggageId(entity.getId());
            event.setBarcode(entity.getBarcode());
            event.setPassengerId(entity.getPassengerId());
            event.setFlightNumber(entity.getFlightNumber());
            event.setOrigin(entity.getOrigin());
            event.setDestination(entity.getDestination());
            event.setStatus(entity.getStatus());
            event.setTimestamp(LocalDateTime.now());
            event.setEventType(eventType);

            String topic = eventType.equals("CREATED") 
                ? KafkaTopicConfig.BAGGAGE_CREATED_TOPIC 
                : KafkaTopicConfig.BAGGAGE_UPDATED_TOPIC;

            kafkaTemplate.send(topic, event);
        } catch (Exception e) {
            // Log but don't fail if Kafka is not available
            System.err.println("Kafka not available: " + e.getMessage());
        }
    }

    private void cacheData(UUID id, String barcode, BaggageResDto dto) {
        // Cache by ID
        String cacheKeyId = CACHE_KEY_PREFIX + id;
        redisTemplate.opsForValue().set(cacheKeyId, dto, CACHE_TTL_HOURS, TimeUnit.HOURS);
        
        // Cache by barcode
        String cacheKeyBarcode = CACHE_KEY_BARCODE_PREFIX + barcode;
        redisTemplate.opsForValue().set(cacheKeyBarcode, dto, CACHE_TTL_HOURS, TimeUnit.HOURS);
    }

    private void invalidateCache(UUID id, String barcode) {
        // Delete cache by ID
        String cacheKeyId = CACHE_KEY_PREFIX + id;
        redisTemplate.delete(cacheKeyId);
        
        // Delete cache by barcode
        String cacheKeyBarcode = CACHE_KEY_BARCODE_PREFIX + barcode;
        redisTemplate.delete(cacheKeyBarcode);
    }
}
