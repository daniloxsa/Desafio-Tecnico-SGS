package br.com.sergipetech.solicitacao_api.repositories;

import br.com.sergipetech.solicitacao_api.entities.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {
}
