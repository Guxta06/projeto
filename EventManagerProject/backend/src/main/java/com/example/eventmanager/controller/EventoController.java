package com.example.eventmanager.controller;

import com.example.eventmanager.model.mysql.Evento;
import com.example.eventmanager.security.JwtUtil;
import com.example.eventmanager.service.EventoService;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService service;
    private final JwtUtil jwtUtil;

    public EventoController(EventoService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    private String getUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        Claims c = jwtUtil.validateAndGetClaims(token);
        return c.getSubject();
    }

    @GetMapping
    public ResponseEntity<List<Evento>> list() { return ResponseEntity.ok(service.listAll()); }

    @PostMapping
    public ResponseEntity<Evento> create(@RequestHeader(value = "Authorization", required = false) String auth, @RequestBody Evento e) {
        String userId = getUserIdFromToken(auth);
        return ResponseEntity.ok(service.create(e, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> get(@PathVariable String id) {
        return service.get(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> update(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable String id, @RequestBody Evento e) {
        String userId = getUserIdFromToken(auth);
        e.setId(id);
        return ResponseEntity.ok(service.update(userId, e));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable String id) {
        String userId = getUserIdFromToken(auth);
        service.delete(userId, id);
        return ResponseEntity.noContent().build();
    }
}
