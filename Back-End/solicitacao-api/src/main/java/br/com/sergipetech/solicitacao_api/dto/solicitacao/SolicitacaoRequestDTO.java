package br.com.sergipetech.solicitacao_api.dto.solicitacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record SolicitacaoRequestDTO(

        @NotNull
        Long solicitanteId,

        @NotNull
        Long categoriaId,

        @NotBlank
        String descricao,

        @NotNull
        @Positive
        BigDecimal valor
) {
}
