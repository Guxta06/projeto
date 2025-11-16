package com.example.eventmanager.controller;

import com.example.eventmanager.model.mysql.Estoque;
import com.example.eventmanager.service.EstoqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {
    private final EstoqueService service;

    public EstoqueController(EstoqueService service) {
        this.service = service;
    }

    @GetMapping
    public List<Estoque> list() {
        return service.listAll();
    }

    @PostMapping
    public ResponseEntity<Estoque> create(@RequestBody Estoque e) {
        Estoque created = service.create(e);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estoque> get(@PathVariable String id) {
        return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Estoque> update(@PathVariable String id, @RequestBody Estoque e) {
        Estoque updated = service.update(id, e);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}