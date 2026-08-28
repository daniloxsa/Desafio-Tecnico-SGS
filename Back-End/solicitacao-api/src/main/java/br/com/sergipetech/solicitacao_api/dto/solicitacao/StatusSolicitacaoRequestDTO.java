package br.com.sergipetech.solicitacao_api.dto.solicitacao;

import br.com.sergipetech.solicitacao_api.enums.StatusSolicitacao;
import jakarta.validation.constraints.NotBlank;

public record StatusSolicitacaoRequestDTO(

        @NotBlank
        StatusSolicitacao statusSolicitacao) {
}
