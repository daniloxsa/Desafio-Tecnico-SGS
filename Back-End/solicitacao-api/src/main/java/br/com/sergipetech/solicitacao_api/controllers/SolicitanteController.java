package br.com.sergipetech.solicitacao_api.controllers;

import br.com.sergipetech.solicitacao_api.dto.solicitante.SolicitanteResponseDTO;
import br.com.sergipetech.solicitacao_api.services.SolicitanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/solicitantes")
public class SolicitanteController {

    @Autowired
    private SolicitanteService service;


    @GetMapping("/{id}")
    public ResponseEntity<SolicitanteResponseDTO> buscarPorId(@PathVariable Long id) {

        SolicitanteResponseDTO response = service.buscarPorId(id);

        return ResponseEntity.ok(response);

    }
}
