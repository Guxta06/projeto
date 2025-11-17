package com.example.eventmanager.model.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.Map;

@Data
@Document("audit_logs")
public class AuditLog {
    @Id
    private String id;

    private String action;
    private String entity;
    private String entityId;
    private String userId;
    private Instant timestamp;
    private Map<String, Object> payload;
}
