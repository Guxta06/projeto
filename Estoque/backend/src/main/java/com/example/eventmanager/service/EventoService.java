package com.example.eventmanager.service;

import com.example.eventmanager.model.mongo.AuditLog;
import com.example.eventmanager.model.mysql.Evento;
import com.example.eventmanager.repository.mongo.AuditLogRepository;
import com.example.eventmanager.repository.mysql.EventoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    private final EventoRepository repo;
    private final AuditLogRepository auditRepo;

    public EventoService(EventoRepository repo, AuditLogRepository auditRepo) {
        this.repo = repo;
        this.auditRepo = auditRepo;
    }

    public Evento create(Evento e, String userId) {
        if (e.getId() == null || e.getId().isBlank()) e.setId("EV" + System.currentTimeMillis());
        e.setCriadoEm(LocalDateTime.now());
        Evento saved = repo.save(e);
        AuditLog log = new AuditLog();
        log.setAction("CREATE_EVENT");
        log.setEntity("eventos");
        log.setEntityId(saved.getId());
        log.setTimestamp(Instant.now());
        log.setUserId(userId);
        log.setPayload(java.util.Map.of("titulo", saved.getTitulo()));
        auditRepo.save(log);
        return saved;
    }

    public List<Evento> listAll() { return repo.findAll(); }

    public Optional<Evento> get(String id) { return repo.findById(id); }

    public Evento update(String userId, Evento e) {
        e.setAtualizadoEm(LocalDateTime.now());
        Evento saved = repo.save(e);
        AuditLog log = new AuditLog();
        log.setAction("UPDATE_EVENT");
        log.setEntity("eventos");
        log.setEntityId(saved.getId());
        log.setTimestamp(Instant.now());
        log.setUserId(userId);
        log.setPayload(java.util.Map.of("titulo", saved.getTitulo()));
        auditRepo.save(log);
        return saved;
    }

    public void delete(String userId, String id) {
        repo.deleteById(id);
        AuditLog log = new AuditLog();
        log.setAction("DELETE_EVENT");
        log.setEntity("eventos");
        log.setEntityId(id);
        log.setTimestamp(Instant.now());
        log.setUserId(userId);
        auditRepo.save(log);
    }
}
