package com.example.eventmanager.repository.mysql;

import com.example.eventmanager.model.mysql.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, String> {
    List<Evento> findByTituloContainingIgnoreCase(String titulo);
}
