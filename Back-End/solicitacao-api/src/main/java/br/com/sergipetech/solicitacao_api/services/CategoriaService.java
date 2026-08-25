package br.com.sergipetech.solicitacao_api.services;

import br.com.sergipetech.solicitacao_api.dto.categoria.CategoriaResponseDTO;
import br.com.sergipetech.solicitacao_api.dto.solicitante.SolicitanteResponseDTO;
import br.com.sergipetech.solicitacao_api.entities.Categoria;
import br.com.sergipetech.solicitacao_api.entities.Solicitante;
import br.com.sergipetech.solicitacao_api.repositories.CategoriaRepository;
import br.com.sergipetech.solicitacao_api.repositories.SolicitanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public CategoriaResponseDTO buscarPorId(Long id) {
        Optional<Categoria> categoriaOptional  = categoriaRepository.findById(id);

        if (categoriaOptional.isEmpty()) {
            throw new RuntimeException("Não encontrado");

        }

        Categoria categoria = categoriaOptional.get();

        CategoriaResponseDTO categoriaResponseDTO = new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNome()
        );

        return categoriaResponseDTO;

    }
}
