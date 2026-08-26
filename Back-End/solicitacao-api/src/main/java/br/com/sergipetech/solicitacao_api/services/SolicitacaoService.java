package br.com.sergipetech.solicitacao_api.services;

import br.com.sergipetech.solicitacao_api.dto.solicitacao.SolicitacaoRequestDTO;
import br.com.sergipetech.solicitacao_api.dto.solicitacao.SolicitacaoResponseDTO;
import br.com.sergipetech.solicitacao_api.entities.Categoria;
import br.com.sergipetech.solicitacao_api.entities.Solicitacao;
import br.com.sergipetech.solicitacao_api.entities.Solicitante;
import br.com.sergipetech.solicitacao_api.enums.StatusSolicitacao;
import br.com.sergipetech.solicitacao_api.repositories.CategoriaRepository;
import br.com.sergipetech.solicitacao_api.repositories.SolicitacaoRepository;
import br.com.sergipetech.solicitacao_api.repositories.SolicitanteRepository;
import br.com.sergipetech.solicitacao_api.services.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SolicitacaoService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private SolicitanteRepository solicitanteRepository;

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;


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
