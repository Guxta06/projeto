package com.example.eventmanager.service;

import com.example.eventmanager.model.mysql.Evento;
import com.example.eventmanager.repository.mysql.EventoRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventoService {

    private final EventoRepository repo;
    private final AuditService auditService;

    public EventoService(EventoRepository repo, AuditService auditService) {
        this.repo = repo;
        this.auditService = auditService;
    }

    public List<Evento> listAll() {
        return repo.findAll();
    }

    public Optional<Evento> get(String id) {
        return repo.findById(id);
    }

    public Evento create(Evento e, String userId) {
        e.setId(UUID.randomUUID().toString());
        e.setCriadoPor(userId);
        e.setCriadoEm(LocalDateTime.now());
        e.setAtualizadoEm(LocalDateTime.now());

        Evento saved = repo.save(e);

        auditService.log(
                "create",
                "evento",
                saved.getId(),
                userId,
                Map.of("titulo", saved.getTitulo())
        );

        return saved;
    }

    public Evento update(String userId, Evento e) {
        e.setAtualizadoEm(LocalDateTime.now());
        Evento saved = repo.save(e);

        auditService.log(
                "update",
                "evento",
                saved.getId(),
                userId,
                Map.of("titulo", saved.getTitulo())
        );

        return saved;
    }

    public void delete(String userId, String id) {
        repo.deleteById(id);

        auditService.log(
                "delete",
                "evento",
                id,
                userId,
                Map.of("deleted", true)
        );
    }
}
