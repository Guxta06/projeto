package com.example.eventmanager.service;

import com.example.eventmanager.model.mongo.AuditLog;
import com.example.eventmanager.repository.mongo.AuditRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class AuditService {

    private final AuditRepository repo;

    public AuditService(AuditRepository repo) {
        this.repo = repo;
    }

    public void log(String action, String entity, String entityId, String userId, Map<String, Object> payload) {
        AuditLog log = new AuditLog();
        log.setId(UUID.randomUUID().toString());
        log.setAction(action);
        log.setEntity(entity);
        log.setEntityId(entityId);
        log.setUserId(userId);
        log.setTimestamp(Instant.now());
        log.setPayload(payload);

        repo.save(log);
    }
}
