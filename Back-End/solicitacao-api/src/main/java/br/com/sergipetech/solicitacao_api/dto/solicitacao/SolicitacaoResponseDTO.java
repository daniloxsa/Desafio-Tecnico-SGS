package br.com.sergipetech.solicitacao_api.dto.solicitacao;

import br.com.sergipetech.solicitacao_api.enums.StatusSolicitacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SolicitacaoResponseDTO(
        Long id,
        String descricao,
        BigDecimal valor,
        LocalDateTime dataSolicitacao,
        StatusSolicitacao status,
        Long solicitanteId,
        String solicitanteNome,
        String solicitanteCpfCnpj,
        Long categoriaId,
        String categoriaNome
) {}