package br.com.sergipetech.solicitacao_api.services;

import br.com.sergipetech.solicitacao_api.dto.solicitante.SolicitanteResponseDTO;
import br.com.sergipetech.solicitacao_api.entities.Solicitante;
import br.com.sergipetech.solicitacao_api.repositories.SolicitanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SolicitanteService {


    @Autowired
    private SolicitanteRepository solicitanteRepository;

    public SolicitanteResponseDTO buscarPorId(Long id) {
        Optional<Solicitante> solicitanteOptional  = solicitanteRepository.findById(id);

        if (solicitanteOptional.isEmpty()) {
            throw new RuntimeException("Não encontrado");

        }

        Solicitante solicitante = solicitanteOptional.get();

        SolicitanteResponseDTO solicitanteResponseDTO = new SolicitanteResponseDTO(
                solicitante.getId(),
                solicitante.getNome(),
                solicitante.getCpfCnpj()
        );

        return solicitanteResponseDTO;

    }
}
