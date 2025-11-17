package com.example.eventmanager.service;

import com.example.eventmanager.model.mysql.Estoque;
import com.example.eventmanager.repository.mysql.EstoqueRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;

@Service
public class EstoqueService {

    private final EstoqueRepository repo;
    private final AuditService auditService;

    public EstoqueService(EstoqueRepository repo, AuditService auditService) {
        this.repo = repo;
        this.auditService = auditService;
    }

    public List<Estoque> listAll() {
        return repo.findAll();
    }

    public Estoque create(Estoque e, String userId) {
        e.setId(UUID.randomUUID().toString());
        Estoque saved = repo.save(e);

        auditService.log(
                "create",
                "estoque",
                saved.getId(),
                userId,
                Map.of("nome", saved.getNome())
        );

        return saved;
    }

    public Optional<Estoque> findById(String id) {
        return repo.findById(id);
    }

    public Estoque update(String id, Estoque e, String userId) {
        e.setId(id);
        Estoque saved = repo.save(e);

        auditService.log(
                "update",
                "estoque",
                saved.getId(),
                userId,
                Map.of("nome", saved.getNome())
        );

        return saved;
    }

    public void delete(String id, String userId) {
        if (!repo.existsById(id)) {
            throw new EmptyResultDataAccessException("Item not found: " + id, 1);
        }

        repo.deleteById(id);

        auditService.log(
                "delete",
                "estoque",
                id,
                userId,
                Map.of("deleted", true)
        );
    }
}
