package com.baggage.service;

import com.baggage.config.KafkaTopicConfig;
import com.baggage.dto.event.TrackingEventDto;
import com.baggage.dto.request.TrackingReqDto;
import com.baggage.dto.response.TrackingResDto;
import com.baggage.mapper.TrackingMapper;
import com.baggage.model.TrackingEntity;
import com.baggage.repository.TrackingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TrackingServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(TrackingServiceImpl.class);

    @Autowired
    private TrackingRepository repository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${baggage.service.url:http://baggage-service:8081}")
    private String baggageServiceUrl;

    private static final String CACHE_KEY_LATEST = "tracking:latest:";
    private static final String CACHE_KEY_HISTORY = "tracking:history:";
    private static final long CACHE_TTL_HOURS = 24;

    public TrackingResDto create(TrackingReqDto request) {
        TrackingEntity entity = TrackingMapper.toEntity(request);
        entity.setTimestamp(LocalDateTime.now());
        entity = repository.save(entity);

        TrackingResDto dto = TrackingMapper.toDto(entity);
        
        // Cache latest tracking
        String cacheKeyLatest = CACHE_KEY_LATEST + entity.getBaggageId();
        redisTemplate.opsForValue().set(cacheKeyLatest, dto, CACHE_TTL_HOURS, TimeUnit.HOURS);
        
        // Invalidate history cache
        String cacheKeyHistory = CACHE_KEY_HISTORY + entity.getBaggageId();
        redisTemplate.delete(cacheKeyHistory);

        // Publish event
        publishTrackingEvent(entity);

        // Update baggage status
        updateBaggageStatus(entity.getBaggageId(), request.getStatus());

        return dto;
    }

    public TrackingResDto getById(UUID id) {
        TrackingEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tracking not found"));
        return TrackingMapper.toDto(entity);
    }

    public List<TrackingResDto> getByBaggageId(UUID baggageId) {
        // Try cache first
        String cacheKey = CACHE_KEY_HISTORY + baggageId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        
        if (cached != null) {
            return objectMapper.convertValue(cached, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, TrackingResDto.class));
        }

        // Cache miss - query database
        List<TrackingResDto> result = repository.findByBaggageIdOrderByTimestampDesc(baggageId).stream()
                .map(TrackingMapper::toDto)
                .collect(Collectors.toList());
        
        // Store in cache
        redisTemplate.opsForValue().set(cacheKey, result, CACHE_TTL_HOURS, TimeUnit.HOURS);
        
        return result;
    }

    public TrackingResDto getLatestByBaggageId(UUID baggageId) {
        String cacheKey = CACHE_KEY_LATEST + baggageId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) return objectMapper.convertValue(cached, TrackingResDto.class);

        TrackingEntity entity = repository.findFirstByBaggageIdOrderByTimestampDesc(baggageId)
                .orElseThrow(() -> new RuntimeException("No tracking found for baggage"));
        TrackingResDto dto = TrackingMapper.toDto(entity);
        redisTemplate.opsForValue().set(cacheKey, dto, CACHE_TTL_HOURS, TimeUnit.HOURS);
        return dto;
    }

    public List<TrackingResDto> getByBarcode(String barcode) {
        UUID baggageId = getBaggageIdByBarcode(barcode);
        return getByBaggageId(baggageId);
    }

    public TrackingResDto getLatestByBarcode(String barcode) {
        UUID baggageId = getBaggageIdByBarcode(barcode);
        return getLatestByBaggageId(baggageId);
    }

    private UUID getBaggageIdByBarcode(String barcode) {
        try {
            String url = baggageServiceUrl + "/api/baggage/barcode/" + barcode;
            String response = restTemplate.getForObject(url, String.class);
            com.fasterxml.jackson.databind.JsonNode data = objectMapper.readTree(response).get("data");
            return UUID.fromString(data.get("id").asText());
        } catch (Exception e) {
            throw new RuntimeException("Baggage not found for barcode: " + barcode);
        }
    }

    private void publishTrackingEvent(TrackingEntity entity) {
        try {
            TrackingEventDto event = new TrackingEventDto();
            event.setTrackingId(entity.getId());
            event.setBaggageId(entity.getBaggageId());
            event.setLocation(entity.getLocation());
            event.setStatus(entity.getStatus());
            event.setTimestamp(entity.getTimestamp());
            event.setRemarks(entity.getRemarks());
            kafkaTemplate.send(KafkaTopicConfig.TRACKING_UPDATED_TOPIC, event);
        } catch (Exception e) {
            log.error("Kafka not available: {}", e.getMessage());
        }
    }

    private void updateBaggageStatus(UUID baggageId, String status) {
        try {
            String url = baggageServiceUrl + "/api/baggage/" + baggageId + "/status?status=" + status;
            log.info("Updating baggage status: {}", url);
            restTemplate.patchForObject(url, null, String.class);
            log.info("Baggage status updated: baggageId={}, status={}", baggageId, status);
        } catch (Exception e) {
            log.error("Failed to update baggage status: {}", e.getMessage());
        }
    }
}
