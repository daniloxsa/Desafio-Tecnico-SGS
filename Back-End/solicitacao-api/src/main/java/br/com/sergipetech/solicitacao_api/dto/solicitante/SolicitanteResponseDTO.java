package br.com.sergipetech.solicitacao_api.dto.solicitante;

import br.com.sergipetech.solicitacao_api.entities.Solicitante;

public record SolicitanteResponseDTO(Long id, String nome, String cpfCnpj) {
}
