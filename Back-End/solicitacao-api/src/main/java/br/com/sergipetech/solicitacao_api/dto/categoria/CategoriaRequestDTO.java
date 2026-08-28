package br.com.sergipetech.solicitacao_api.dto.categoria;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequestDTO(

        @NotBlank
        String nome) {
}
