package com.techservices.api.dto;

public class ConsultaClienteResponse {

    private String message;
    private ClienteDto data;
    private Integer tokensRestantes;

    public ConsultaClienteResponse() {
    }

    public ConsultaClienteResponse(String message, ClienteDto data, Integer tokensRestantes) {
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

    public ClienteDto getData() {
        return data;
    }

    public void setData(ClienteDto data) {
        this.data = data;
    }

    public Integer getTokensRestantes() {
        return tokensRestantes;
    }

    public void setTokensRestantes(Integer tokensRestantes) {
        this.tokensRestantes = tokensRestantes;
    }
}
