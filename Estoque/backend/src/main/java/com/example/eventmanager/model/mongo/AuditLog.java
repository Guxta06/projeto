package com.example.eventmanager.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Document(collection = "audit_logs")
@Data
public class AuditLog {
    @Id
    private String id;
    private String userId;
    private String action;
    private String entity;
    private String entityId;
    private Instant timestamp;
    private Map<String, Object> payload;
}
