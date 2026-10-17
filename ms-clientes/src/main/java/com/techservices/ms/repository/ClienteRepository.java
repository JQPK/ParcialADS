package com.techservices.ms.repository;

import com.techservices.ms.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByDni(String dni);
    boolean existsByDni(String dni);
    boolean existsByCorreoElectronico(String correoElectronico);
    boolean existsByDniAndIdNot(String dni, Long id);
    boolean existsByCorreoElectronicoAndIdNot(String correoElectronico, Long id);
}
