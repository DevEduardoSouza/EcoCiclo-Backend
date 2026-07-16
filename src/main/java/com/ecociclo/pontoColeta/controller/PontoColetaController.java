package com.ecociclo.pontoColeta.controller;

import com.ecociclo.pontoColeta.model.PontoColeta;
import com.ecociclo.pontoColeta.service.PontoColetaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/pontos-coleta")
public class PontoColetaController {

    private final PontoColetaService service;

    public PontoColetaController(PontoColetaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody PontoColeta pontoColeta) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(pontoColeta));
        } catch (IllegalArgumentException e) {
            return erroRequisicao(e);
        }
    }

    @GetMapping
    public ResponseEntity<?> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable String id) {
        try {
            PontoColeta pontoColeta = service.buscarPorId(id);
            if (pontoColeta == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(pontoColeta);
        } catch (IllegalArgumentException e) {
            return erroRequisicao(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable String id, @RequestBody PontoColeta pontoColeta) {
        try {
            return ResponseEntity.ok(service.atualizar(id, pontoColeta));
        } catch (IllegalArgumentException e) {
            return respostaErro(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable String id) {
        try {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return respostaErro(e);
        }
    }

    @PutMapping("/{id}/ativar")
    public ResponseEntity<?> ativar(@PathVariable String id) {
        try {
            return ResponseEntity.ok(service.ativar(id));
        } catch (IllegalArgumentException e) {
            return respostaErro(e);
        }
    }

    @PutMapping("/{id}/desativar")
    public ResponseEntity<?> desativar(@PathVariable String id) {
        try {
            return ResponseEntity.ok(service.desativar(id));
        } catch (IllegalArgumentException e) {
            return respostaErro(e);
        }
    }

    private ResponseEntity<?> respostaErro(IllegalArgumentException e) {
        if (e.getMessage() != null && e.getMessage().toLowerCase().contains("nao encontrado")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }

        return erroRequisicao(e);
    }

    private ResponseEntity<?> erroRequisicao(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
    }
}
