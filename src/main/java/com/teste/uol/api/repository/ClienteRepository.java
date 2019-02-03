package com.teste.uol.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teste.uol.api.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long>{

}
