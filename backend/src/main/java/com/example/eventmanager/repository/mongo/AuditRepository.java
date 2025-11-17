package com.example.eventmanager.repository.mongo;

import com.example.eventmanager.model.mongo.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditRepository extends MongoRepository<AuditLog, String> {
}
