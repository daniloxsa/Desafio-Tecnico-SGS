package br.com.sergipetech.solicitacao_api.services;

import br.com.sergipetech.solicitacao_api.dto.solicitacao.SolicitacaoRequestDTO;
import br.com.sergipetech.solicitacao_api.dto.solicitacao.SolicitacaoResponseDTO;
import br.com.sergipetech.solicitacao_api.dto.solicitante.SolicitanteRequestDTO;
import br.com.sergipetech.solicitacao_api.dto.solicitante.SolicitanteResponseDTO;
import br.com.sergipetech.solicitacao_api.entities.Solicitacao;
import br.com.sergipetech.solicitacao_api.entities.Solicitante;
import br.com.sergipetech.solicitacao_api.repositories.SolicitanteRepository;
import br.com.sergipetech.solicitacao_api.services.exception.InvalidDocumentException;
import br.com.sergipetech.solicitacao_api.services.exception.ResourceNotFoundException;
import br.com.sergipetech.solicitacao_api.services.utilities.DocumentoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SolicitanteService {


    @Autowired
    private SolicitanteRepository solicitanteRepository;


    public List<SolicitanteResponseDTO> buscarTodos() {
        List<Solicitante> solicitantes = solicitanteRepository.findAll();

        if (solicitantes.isEmpty()) {
            throw new ResourceNotFoundException("Não há registros dessa entidade no banco de dados");
        }

        List<SolicitanteResponseDTO> responseDTO = solicitantes.stream()
                .map(s -> new SolicitanteResponseDTO(
                        s.getId(),
                        s.getNome(),
                        s.getCpfCnpj()
                )).toList();

        return responseDTO;

    }

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

        if (!DocumentoValidator.isValido(request.cpfCnpj())) {
            throw new InvalidDocumentException(request.cpfCnpj());
        }


        if (solicitanteRepository.existsByCpfCnpj(request.cpfCnpj())) {
            throw new DataIntegrityViolationException("CPF/CNPJ já cadastrado");
        }

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
