package com.example.eventmanager.service;

import com.example.eventmanager.model.mysql.Estoque;
import com.example.eventmanager.repository.mysql.EstoqueRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EstoqueService {
    private final EstoqueRepository repo;

    public EstoqueService(EstoqueRepository repo) {
        this.repo = repo;
    }

    public List<Estoque> listAll() {
        return repo.findAll();
    }

    public Estoque create(Estoque e) {
        if (e.getId() == null || e.getId().isEmpty()) {
            e.setId(UUID.randomUUID().toString());
        }
        return repo.save(e);
    }

    public Optional<Estoque> findById(String id) {
        return repo.findById(id);
    }

    public Estoque update(String id, Estoque e) {
        e.setId(id);
        return repo.save(e);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}