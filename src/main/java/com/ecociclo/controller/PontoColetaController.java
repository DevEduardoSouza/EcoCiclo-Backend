package com.ecociclo.controller;

import org.springframework.web.bind.annotation.*;
import com.ecociclo.model.PontoColeta;
import com.ecociclo.service.PontoColetaService;
import org.springframework.http.ResponseEntity;
import java.util.List;
// TODO: Implementação será discutida com a equipe
@RestController
@RequestMapping("/api/pontos-coleta")
public class PontoColetaController {
    
    private final PontoColetaService service;

    public PontoColetaController(PontoColetaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PontoColeta> salvar(@RequestBody PontoColeta pontoColeta) {
        return ResponseEntity.ok(service.salvar(pontoColeta));
    }

    @GetMapping
    public ResponseEntity<List<PontoColeta>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PontoColeta> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PontoColeta> atualizar(
            @PathVariable String id,
            @RequestBody PontoColeta pontoColeta
    ) {
        return ResponseEntity.ok(service.atualizar(id, pontoColeta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/ativar")
    public ResponseEntity<PontoColeta> ativar(@PathVariable String id) {
        return ResponseEntity.ok(service.ativar(id));
    }

    @PutMapping("/{id}/desativar")
    public ResponseEntity<PontoColeta> desativar(@PathVariable String id) {
        return ResponseEntity.ok(service.desativar(id));
    }
}
