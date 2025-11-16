package com.example.eventmanager.repository.mysql;

import com.example.eventmanager.model.mysql.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, String> {
}