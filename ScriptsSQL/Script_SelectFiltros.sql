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
      AND (:valorMin IS NULL OR s.valor >= :valorMin)
      AND (:valorMax IS NULL OR s.valor <= :valorMax)