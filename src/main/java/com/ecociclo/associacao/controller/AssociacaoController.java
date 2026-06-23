package com.ecociclo.associacao.controller;

import com.ecociclo.associacao.model.Associacao;
import com.ecociclo.associacao.model.StatusAssociacao;
import com.ecociclo.associacao.service.AssociacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/associacoes")
public class AssociacaoController {

    private final AssociacaoService service;

    public AssociacaoController(AssociacaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Associacao associacao) throws ExecutionException, InterruptedException {
        try {
            String id = service.criar(associacao);
            return ResponseEntity.ok(Map.of("id", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> listar() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable String id) throws ExecutionException, InterruptedException {
        Associacao associacao = service.buscarPorId(id);
        if (associacao == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(associacao);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable String id, @RequestBody Associacao associacao)
            throws ExecutionException, InterruptedException {
        try {
            service.atualizar(id, associacao);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable String id, @RequestBody Map<String, String> body)
            throws ExecutionException, InterruptedException {
        try {
            StatusAssociacao status = StatusAssociacao.fromString(body.get("status"));
            service.atualizarStatus(id, status);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "erro", "Status invalido. Valores aceitos: ok, negado, pendente."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable String id) throws ExecutionException, InterruptedException {
        try {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }
}
