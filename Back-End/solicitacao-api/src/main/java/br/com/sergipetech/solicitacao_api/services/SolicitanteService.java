package br.com.sergipetech.solicitacao_api.services;

import br.com.sergipetech.solicitacao_api.dto.solicitacao.SolicitacaoRequestDTO;
import br.com.sergipetech.solicitacao_api.dto.solicitante.SolicitanteRequestDTO;
import br.com.sergipetech.solicitacao_api.dto.solicitante.SolicitanteResponseDTO;
import br.com.sergipetech.solicitacao_api.entities.Solicitacao;
import br.com.sergipetech.solicitacao_api.entities.Solicitante;
import br.com.sergipetech.solicitacao_api.repositories.SolicitanteRepository;
import br.com.sergipetech.solicitacao_api.services.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SolicitanteService {


    @Autowired
    private SolicitanteRepository solicitanteRepository;

    public SolicitanteResponseDTO buscarPorId(Long id) {
        Optional<Solicitante> solicitanteOptional  = solicitanteRepository.findById(id);

        if (solicitanteOptional.isEmpty()) {
            throw new ResourceNotFoundException(id);

        }

        Solicitante solicitante = solicitanteOptional.get();

        SolicitanteResponseDTO solicitanteResponseDTO = new SolicitanteResponseDTO(
                solicitante.getId(),
                solicitante.getNome(),
                solicitante.getCpfCnpj()
        );

        return solicitanteResponseDTO;

    }

    public SolicitanteResponseDTO criarSolicitante(SolicitanteRequestDTO request) {

        Solicitante solicitante = new Solicitante(
                request.nome(),
                request.cpfCnpj()
        );

        solicitanteRepository.save(solicitante);

        SolicitanteResponseDTO solicitanteResponseDTO = new SolicitanteResponseDTO(
                solicitante.getId(),
                solicitante.getNome(),
                solicitante.getCpfCnpj()

        );

        return solicitanteResponseDTO;
    }


    public void deletarPorId(Long id) {

        if (!solicitanteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Solicitação não encontrada");
        }

        solicitanteRepository.deleteById(id);

        // o proprio spring lança a excessao de violação de integridade "DataIntegrityViolationException"

    }


}
