package br.com.sergipetech.solicitacao_api.repositories.queries;

import br.com.sergipetech.solicitacao_api.enums.StatusSolicitacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface SolicitacaoProjection {

    Long getId();
    String getDescricao();
    BigDecimal getValor();
    LocalDateTime getDataSolicitacao();
    StatusSolicitacao getStatus();

    Long getSolicitanteId();
    String getSolicitanteNome();
    String getSolicitanteCpfCnpj();

    Long getCategoriaId();
    String getCategoriaNome();
}