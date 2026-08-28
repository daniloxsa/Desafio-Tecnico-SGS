package br.com.sergipetech.solicitacao_api.dto.solicitante;


import jakarta.validation.constraints.NotBlank;

public record SolicitanteRequestDTO(

        @NotBlank
        String nome,

        @NotBlank
        String cpfCnpj) {
}
