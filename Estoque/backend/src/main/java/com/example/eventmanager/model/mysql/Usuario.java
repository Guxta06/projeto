package com.example.eventmanager.model.mysql;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {
    @Id
    private String id;
    private String nome;
    @Column(unique = true)
    private String email;
    private String senhaHash;
    @Column(name = "grupo_id")
    private String grupoId;
    private Instant criadoEm;
}
