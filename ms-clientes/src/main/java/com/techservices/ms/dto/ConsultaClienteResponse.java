package com.techservices.ms.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de consulta individual de cliente con saldo de tokens restantes")
public class ConsultaClienteResponse {

    @Schema(description = "Mensaje descriptivo del resultado", example = "Consulta realizada correctamente")
    private String message;

    @Schema(description = "Datos del cliente consultado")
    private ClienteResponseDto data;

    @Schema(description = "Cantidad de tokens de consulta restantes tras el consumo", example = "9")
    private Integer tokensRestantes;

    public ConsultaClienteResponse() {
    }

    public ConsultaClienteResponse(String message, ClienteResponseDto data, Integer tokensRestantes) {
        this.message = message;
        this.data = data;
        this.tokensRestantes = tokensRestantes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ClienteResponseDto getData() {
        return data;
    }

    public void setData(ClienteResponseDto data) {
        this.data = data;
    }

    public Integer getTokensRestantes() {
        return tokensRestantes;
    }

    public void setTokensRestantes(Integer tokensRestantes) {
        this.tokensRestantes = tokensRestantes;
    }
}
