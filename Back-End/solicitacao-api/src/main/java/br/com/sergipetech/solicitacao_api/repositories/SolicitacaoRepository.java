package br.com.sergipetech.solicitacao_api.repositories;

import br.com.sergipetech.solicitacao_api.dto.solicitacao.SolicitacaoResponseDTO;
import br.com.sergipetech.solicitacao_api.entities.Solicitacao;
import br.com.sergipetech.solicitacao_api.enums.StatusSolicitacao;
import br.com.sergipetech.solicitacao_api.repositories.queries.SolicitacaoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {


    @Query(value = """
    SELECT s.id, s.descricao, s.valor, s.data_solicitacao, s.status_solicitacao AS status, so.id AS solicitante_id, so.nome AS solicitante_nome, so.cpf_cnpj AS solicitanteCpfCnpj, c.id AS categoria_id, c.nome AS categoria_nome
    FROM solicitacao s
    JOIN solicitante so
        ON so.id = s.id_solicitante
    JOIN categoria c
        ON c.id = s.id_categoria
    WHERE (:status IS NULL OR s.status_solicitacao = :status)
      AND (:categoriaId IS NULL OR s.id_categoria = :categoriaId)
      AND (:dataInicio IS NULL OR s.data_solicitacao >= :dataInicio)
      AND (:dataFim IS NULL OR s.data_solicitacao <= :dataFim)
    """, nativeQuery = true)
    List<SolicitacaoProjection> listarSolicitacoes(
            @Param("status") String status,
            @Param("categoriaId") Long categoriaId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}
