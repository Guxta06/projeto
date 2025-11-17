package com.example.eventmanager.controller;

import com.example.eventmanager.model.mysql.Estoque;
import com.example.eventmanager.security.JwtUtil;
import com.example.eventmanager.service.EstoqueService;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    private final EstoqueService service;
    private final JwtUtil jwtUtil;

    public EstoqueController(EstoqueService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    private String getUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        Claims c = jwtUtil.validateAndGetClaims(authHeader.substring(7));
        return c.getSubject();
    }

    @GetMapping
    public ResponseEntity<List<Estoque>> list() {
        return ResponseEntity.ok(service.listAll());
    }

    @PostMapping
    public ResponseEntity<Estoque> create(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestBody Estoque e
    ) {
        String userId = getUserIdFromToken(auth);
        return ResponseEntity.ok(service.create(e, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Estoque> update(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @PathVariable("id") String id,
            @RequestBody Estoque e
    ) {
        String userId = getUserIdFromToken(auth);
        return ResponseEntity.ok(service.update(id, e, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @PathVariable("id") String id
    ) {
        String userId = getUserIdFromToken(auth);
        service.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
