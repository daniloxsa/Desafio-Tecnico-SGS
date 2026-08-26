package br.com.sergipetech.solicitacao_api.controllers;

import br.com.sergipetech.solicitacao_api.dto.solicitacao.SolicitacaoRequestDTO;
import br.com.sergipetech.solicitacao_api.dto.solicitacao.SolicitacaoResponseDTO;
import br.com.sergipetech.solicitacao_api.dto.solicitante.SolicitanteResponseDTO;
import br.com.sergipetech.solicitacao_api.services.SolicitacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/solicitacoes")
public class SolicitacaoController {

    @Autowired
    private SolicitacaoService service;




    @PostMapping
    public ResponseEntity<SolicitacaoResponseDTO> criarSolicitacao(@RequestBody SolicitacaoRequestDTO solicitacaoDTO) {
        SolicitacaoResponseDTO responseDTO = service.criarSolicitacao(solicitacaoDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(responseDTO.id()).toUri();

        return ResponseEntity.created(uri).body(responseDTO);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPorId(@PathVariable Long id) {

        service.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }

}
