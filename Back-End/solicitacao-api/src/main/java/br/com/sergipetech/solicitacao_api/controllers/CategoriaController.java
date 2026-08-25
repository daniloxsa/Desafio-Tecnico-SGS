package br.com.sergipetech.solicitacao_api.controllers;

import br.com.sergipetech.solicitacao_api.dto.categoria.CategoriaResponseDTO;
import br.com.sergipetech.solicitacao_api.dto.solicitante.SolicitanteResponseDTO;
import br.com.sergipetech.solicitacao_api.services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService service;


    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(@PathVariable Long id) {

        CategoriaResponseDTO response = service.buscarPorId(id);

        return ResponseEntity.ok(response);

    }
}
