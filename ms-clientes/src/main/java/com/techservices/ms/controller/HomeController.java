package com.techservices.ms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * Redirige la raíz "/" automáticamente a la interfaz gráfica interactiva de Swagger UI
     */
    @GetMapping("/")
    public String redirigirASwagger() {
        return "redirect:/swagger-ui/index.html";
    }
}
