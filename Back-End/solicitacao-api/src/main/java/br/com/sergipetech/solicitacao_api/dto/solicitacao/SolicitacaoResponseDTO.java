package br.com.sergipetech.solicitacao_api.dto.solicitacao;

import br.com.sergipetech.solicitacao_api.enums.StatusSolicitacao;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SolicitacaoResponseDTO(
        Long id,
        String descricao,
        BigDecimal valor,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataSolicitacao,

        StatusSolicitacao status,
        Long solicitanteId,
        String solicitanteNome,
        String solicitanteCpfCnpj,
        Long categoriaId,
        String categoriaNome
) {}