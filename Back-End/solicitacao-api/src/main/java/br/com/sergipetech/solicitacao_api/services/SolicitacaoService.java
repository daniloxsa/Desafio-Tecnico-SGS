package br.com.sergipetech.solicitacao_api.services;

import br.com.sergipetech.solicitacao_api.dto.solicitacao.SolicitacaoRequestDTO;
import br.com.sergipetech.solicitacao_api.dto.solicitacao.SolicitacaoResponseDTO;
import br.com.sergipetech.solicitacao_api.dto.solicitacao.StatusSolicitacaoRequestDTO;
import br.com.sergipetech.solicitacao_api.entities.Categoria;
import br.com.sergipetech.solicitacao_api.entities.Solicitacao;
import br.com.sergipetech.solicitacao_api.entities.Solicitante;
import br.com.sergipetech.solicitacao_api.enums.StatusSolicitacao;
import br.com.sergipetech.solicitacao_api.repositories.CategoriaRepository;
import br.com.sergipetech.solicitacao_api.repositories.SolicitacaoRepository;
import br.com.sergipetech.solicitacao_api.repositories.SolicitanteRepository;
import br.com.sergipetech.solicitacao_api.repositories.queries.SolicitacaoProjection;
import br.com.sergipetech.solicitacao_api.services.exception.ResourceNotFoundException;
import br.com.sergipetech.solicitacao_api.services.exception.StatusTransactionError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitacaoService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private SolicitanteRepository solicitanteRepository;

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;


    public List<SolicitacaoResponseDTO> listarSolicitacoes(
            StatusSolicitacao status,
            Long categoriaId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            BigDecimal valorMin,
            BigDecimal valorMax
    ) {

        List<SolicitacaoProjection> solicitacaoProjection =
                solicitacaoRepository.listarSolicitacoes(
                        status != null ? status.name() : null,
                        categoriaId,
                        dataInicio,
                        dataFim,
                        valorMin,
                        valorMax

                );

        List<SolicitacaoResponseDTO> responseDTOS = solicitacaoProjection.stream()
                .map(s -> new SolicitacaoResponseDTO(
                        s.getId(),
                        s.getDescricao(),
                        s.getValor(),
                        s.getDataSolicitacao(),
                        s.getStatus(),
                        s.getSolicitanteId(),
                        s.getSolicitanteNome(),
                        s.getSolicitanteCpfCnpj(),
                        s.getCategoriaId(),
                        s.getCategoriaNome()
                ))
                .toList();

        return responseDTOS;
    }



    public SolicitacaoResponseDTO criarSolicitacao(SolicitacaoRequestDTO request) {

        Solicitante solicitante = validarSolicitante(request);
        Categoria categoria = validarCategoria((request));

        Solicitacao solicitacao = new Solicitacao();

        solicitacao.setDescricao(request.descricao());
        solicitacao.setValor(request.valor());
        solicitacao.setSolicitante(solicitante);
        solicitacao.setCategoria(categoria);


        solicitacao.setData_solicitacao(LocalDateTime.now());

        solicitacaoRepository.save(solicitacao);


        return new SolicitacaoResponseDTO(
                solicitacao.getId(),
                solicitacao.getDescricao(),
                solicitacao.getValor(),
                solicitacao.getData_solicitacao(),
                solicitacao.getStatusSolicitacao(),
                solicitante.getId(),
                solicitante.getNome(),
                solicitante.getCpfCnpj(),
                categoria.getId(),
                categoria.getNome()
        );


    }

    public void deletarPorId(Long id) {

        if (!solicitacaoRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }

        solicitacaoRepository.deleteById(id);

    }

    public SolicitacaoResponseDTO alterarStatus(Long id, StatusSolicitacaoRequestDTO request) {
        Optional<Solicitacao> solicitacaoOptional = solicitacaoRepository.findById(id) ;

        if (solicitacaoOptional.isEmpty()) {
            throw new ResourceNotFoundException(id);
        }

        Solicitacao solicitacao = solicitacaoOptional.get();

        StatusSolicitacao statusAtual = solicitacao.getStatusSolicitacao();
        StatusSolicitacao novoStatus = request.statusSolicitacao();
        

        validarTransicao(statusAtual, novoStatus);

        solicitacao.setStatusSolicitacao(novoStatus);

        solicitacaoRepository.save(solicitacao);

        return new SolicitacaoResponseDTO(
                solicitacao.getId(),
                solicitacao.getDescricao(),
                solicitacao.getValor(),
                solicitacao.getData_solicitacao(),
                solicitacao.getStatusSolicitacao(),
                solicitacao.getSolicitante().getId(),
                solicitacao.getSolicitante().getNome(),
                solicitacao.getSolicitante().getCpfCnpj(),
                solicitacao.getCategoria().getId(),
                solicitacao.getCategoria().getNome()
        );

    }




    private void validarTransicao(StatusSolicitacao atual, StatusSolicitacao novo) {
        if (atual == StatusSolicitacao.SOLICITADO) {
            if (novo != StatusSolicitacao.LIBERADO && novo != StatusSolicitacao.REJEITADO) {
                throw new StatusTransactionError("Violação na hierarquia de transição de status");
            }
        } else if (atual == StatusSolicitacao.LIBERADO) {
            if (novo != StatusSolicitacao.APROVADO && novo != StatusSolicitacao.REJEITADO) {
                throw new StatusTransactionError("Violação na hierarquia de transição de status");
            }
        } else if (atual == StatusSolicitacao.APROVADO) {
            if ((novo != StatusSolicitacao.CANCELADO)) {
                throw new StatusTransactionError("Violação na hierarquia de transição de status");
            }
        } else if (atual == StatusSolicitacao.REJEITADO || atual == StatusSolicitacao.CANCELADO) {
            throw new StatusTransactionError("Violação na hierarquia de transição de status");
        }
    }


    private Solicitante validarSolicitante(SolicitacaoRequestDTO request) {
        Optional<Solicitante> solicitante = solicitanteRepository.findById(request.solicitanteId());

        if (solicitante.isEmpty()) {
            throw new RuntimeException("Solicitante não encontrado");
        }

        return solicitante.get();

    }

    private Categoria validarCategoria(SolicitacaoRequestDTO request) {
        Optional<Categoria> categoria = categoriaRepository.findById(request.categoriaId());

        if (categoria.isEmpty()) {
            throw new RuntimeException("Solicitante não encontrado");
        }

       return categoria.get();

    }
}
