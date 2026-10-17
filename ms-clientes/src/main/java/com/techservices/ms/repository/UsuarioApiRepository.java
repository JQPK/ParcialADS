package com.techservices.ms.repository;

import com.techservices.ms.model.UsuarioApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioApiRepository extends JpaRepository<UsuarioApi, Long> {

    Optional<UsuarioApi> findByApiKey(String apiKey);

    Optional<UsuarioApi> findByApiKeyAndTokenAutenticacion(String apiKey, String tokenAutenticacion);

    @Modifying
    @Query("UPDATE UsuarioApi u SET u.tokensDisponibles = u.tokensDisponibles - 1 WHERE u.id = :id AND u.tokensDisponibles > 0")
    int decrementarToken(@Param("id") Long id);

    @Query("SELECT u.tokensDisponibles FROM UsuarioApi u WHERE u.id = :id")
    Optional<Integer> findTokensDisponiblesById(@Param("id") Long id);
}
