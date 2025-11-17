package com.example.eventmanager.model.mysql;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "grupos_usuarios")
@Data
public class GrupoUsuario {
    @Id
    private String id;
    private String nome;
    private String descricao;
}
