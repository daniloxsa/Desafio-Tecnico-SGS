package br.com.sergipetech.solicitacao_api.dto.solicitacao;

import java.math.BigDecimal;

public record SolicitacaoRequestDTO(
        Long solicitanteId,
        Long categoriaId,
        String descricao,
        BigDecimal valor
) {
}
