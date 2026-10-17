package com.techservices.ms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Respuesta de listado de clientes con saldo de tokens restantes")
public class ConsultaListaClientesResponse {

    @Schema(description = "Mensaje informativo", example = "Listado de clientes obtenido correctamente")
    private String message;

    @Schema(description = "Total de registros retornados", example = "6")
    private int total;

    @Schema(description = "Lista de clientes registrados")
    private List<ClienteResponseDto> data;

    @Schema(description = "Tokens restantes del usuario tras la consulta de listado", example = "8")
    private Integer tokensRestantes;

    public ConsultaListaClientesResponse() {
    }

    public ConsultaListaClientesResponse(String message, int total, List<ClienteResponseDto> data, Integer tokensRestantes) {
        this.message = message;
        this.total = total;
        this.data = data;
        this.tokensRestantes = tokensRestantes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ClienteResponseDto> getData() {
        return data;
    }

    public void setData(List<ClienteResponseDto> data) {
        this.data = data;
    }

    public Integer getTokensRestantes() {
        return tokensRestantes;
    }

    public void setTokensRestantes(Integer tokensRestantes) {
        this.tokensRestantes = tokensRestantes;
    }
}
