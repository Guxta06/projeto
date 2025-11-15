package com.example.eventmanager.controller;

import com.example.eventmanager.model.mysql.Usuario;
import com.example.eventmanager.security.JwtUtil;
import com.example.eventmanager.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    public AuthController(UsuarioService usuarioService, JwtUtil jwtUtil) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        String senha = body.get("senha");

        // REMOVE var → usa Optional<Usuario>
        Optional<Usuario> opt = usuarioService.findByEmail(email);

        if (!opt.isPresent()) {
            return ResponseEntity.status(401).body(
                    Map.of("error", "invalid_credentials")
            );
        }

        Usuario u = opt.get();

        if (!usuarioService.checkPassword(u, senha)) {
            return ResponseEntity.status(401).body(
                    Map.of("error", "invalid_credentials")
            );
        }

        String token = jwtUtil.generateToken(u.getId(), u.getEmail());

        return ResponseEntity.ok(
                Map.of("token", token, "userId", u.getId(), "email", u.getEmail())
        );
    }
}
