package com.example.eventmanager.service;

import com.example.eventmanager.model.mysql.Usuario;
import com.example.eventmanager.repository.mysql.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    public Optional<Usuario> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public boolean checkPassword(Usuario user, String senha) {
        return encoder.matches(senha, user.getSenhaHash());
    }

    public Usuario create(String nome, String email, String senha, String grupoId) {
        Usuario u = new Usuario();
        u.setId(UUID.randomUUID().toString());
        u.setNome(nome);
        u.setEmail(email);
        u.setSenhaHash(encoder.encode(senha));
        u.setGrupoId(grupoId);
        u.setCriadoEm(Instant.now());
        return repo.save(u);
    }
}
