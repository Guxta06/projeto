package com.example.eventmanager.service;

import com.example.eventmanager.model.mysql.Usuario;
import com.example.eventmanager.repository.mysql.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    public Usuario create(Usuario u, String rawPassword) {
        u.setSenhaHash(encoder.encode(rawPassword));
        u.setCriadoEm(Instant.now());
        return repo.save(u);
    }

    public Optional<Usuario> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public boolean checkPassword(Usuario u, String rawPassword) {
        return encoder.matches(rawPassword, u.getSenhaHash());
    }
}
