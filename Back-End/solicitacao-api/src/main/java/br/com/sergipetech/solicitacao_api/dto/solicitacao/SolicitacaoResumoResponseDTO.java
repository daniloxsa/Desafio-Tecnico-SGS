package br.com.sergipetech.solicitacao_api.dto.solicitacao;

import br.com.sergipetech.solicitacao_api.enums.StatusSolicitacao;

import java.math.BigDecimal;

public record SolicitacaoResumoResponseDTO(
        Long id,
        String categoriaNome,
        BigDecimal valor,
        StatusSolicitacao status
) {
}
