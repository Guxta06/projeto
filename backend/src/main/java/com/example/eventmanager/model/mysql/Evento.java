package com.example.eventmanager.model.mysql;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "eventos")
@Data
public class Evento {
    @Id
    private String id;
    private String titulo;
    @Column(columnDefinition = "TEXT")
    private String descricao;
    private LocalDateTime dataEvento;
    private String criadoPor;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
