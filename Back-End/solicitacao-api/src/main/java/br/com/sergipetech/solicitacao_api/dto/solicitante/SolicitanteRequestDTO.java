package br.com.sergipetech.solicitacao_api.dto.solicitante;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SolicitanteRequestDTO(

        @NotBlank
        @Pattern(regexp = "^[\\p{L} ]+$", message = "Nome deve conter apenas letras e espaços")
        String nome,

        @NotBlank
        String cpfCnpj) {
}