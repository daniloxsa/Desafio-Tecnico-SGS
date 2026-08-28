package br.com.sergipetech.solicitacao_api.controllers;

import br.com.sergipetech.solicitacao_api.dto.solicitacao.SolicitacaoRequestDTO;
import br.com.sergipetech.solicitacao_api.dto.solicitacao.SolicitacaoResponseDTO;
import br.com.sergipetech.solicitacao_api.dto.solicitacao.StatusSolicitacaoRequestDTO;
import br.com.sergipetech.solicitacao_api.enums.StatusSolicitacao;
import br.com.sergipetech.solicitacao_api.services.SolicitacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/solicitacoes")
public class SolicitacaoController {

    @Autowired
    private SolicitacaoService service;


    @GetMapping
    public ResponseEntity<List<SolicitacaoResponseDTO>> listarSolicitacoes (
            @RequestParam(required = false) StatusSolicitacao status,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) LocalDateTime dataInicio,
            @RequestParam(required = false) LocalDateTime dataFim,
            @RequestParam(required = false) BigDecimal valorMin,
            @RequestParam(required = false) BigDecimal valorMax
    ) {


        List<SolicitacaoResponseDTO> responseDTOS = service.listarSolicitacoes(
                status,
                categoriaId,
                dataInicio,
                dataFim,
                valorMin,
                valorMax

        );

        return ResponseEntity.ok().body(responseDTOS);


    }




    @PostMapping
    public ResponseEntity<SolicitacaoResponseDTO> criarSolicitacao(@Valid @RequestBody SolicitacaoRequestDTO solicitacaoDTO) {
        SolicitacaoResponseDTO responseDTO = service.criarSolicitacao(solicitacaoDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(responseDTO.id()).toUri();

        return ResponseEntity.created(uri).body(responseDTO);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPorId(@PathVariable Long id) {

        service.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<SolicitacaoResponseDTO> alterarStatus(@Valid @PathVariable Long id, @RequestBody StatusSolicitacaoRequestDTO requestDTO) {

        SolicitacaoResponseDTO responseDTO = service.alterarStatus(id, requestDTO);

        return ResponseEntity.ok(responseDTO);
    }

}
