package com.techservices.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> inicio() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("empresa", "TechServices Perú");
        info.put("proyecto", "api-techservices (Ejercicio 1 - Examen Parcial N.° 01)");
        info.put("estado", "Activo y Operativo");
        info.put("endpointObligatorio", "GET /api/clientes/{id}");
        info.put("cabecerasRequeridas", "x-api-key y Authorization: Bearer <token>");
        info.put("mensaje", "Utilice Postman Web con las cabeceras requeridas para consultar clientes.");
        return ResponseEntity.ok(info);
    }
}
