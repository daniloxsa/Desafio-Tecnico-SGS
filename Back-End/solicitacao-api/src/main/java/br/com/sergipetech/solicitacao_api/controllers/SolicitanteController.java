package br.com.sergipetech.solicitacao_api.controllers;

import br.com.sergipetech.solicitacao_api.dto.solicitante.SolicitanteRequestDTO;
import br.com.sergipetech.solicitacao_api.dto.solicitante.SolicitanteResponseDTO;
import br.com.sergipetech.solicitacao_api.services.SolicitanteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/solicitantes")
public class SolicitanteController {

    @Autowired
    private SolicitanteService service;

    @GetMapping
    public ResponseEntity<List<SolicitanteResponseDTO>> buscarTodos() {

        List<SolicitanteResponseDTO> responseDTOS = service.buscarTodos();

        return ResponseEntity.ok().body(responseDTOS);

    }


    @GetMapping("/{id}")
    public ResponseEntity<SolicitanteResponseDTO> buscarPorId(@PathVariable Long id) {

        SolicitanteResponseDTO response = service.buscarPorId(id);

        return ResponseEntity.ok(response);

    }

    @PostMapping
    public ResponseEntity<SolicitanteResponseDTO> criarSolicitante(@Valid @RequestBody SolicitanteRequestDTO requestDTO) {
        SolicitanteResponseDTO responseDTO = service.criarSolicitante(requestDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(responseDTO.id()).toUri();

        return ResponseEntity.created(uri).body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPorId(@PathVariable Long id) {
        service.deletarPorId(id);

        return ResponseEntity.noContent().build();
    }



}
