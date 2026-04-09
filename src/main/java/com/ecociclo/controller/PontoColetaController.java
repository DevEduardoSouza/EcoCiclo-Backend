package com.ecociclo.controller;

import com.ecociclo.model.PontoColeta;
import com.ecociclo.service.PontoColetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/pontos-coleta")
public class PontoColetaController {

    @Autowired
    private PontoColetaService pontoColetaService;

    @PostMapping
    public ResponseEntity<String> criar(@RequestBody PontoColeta pontoColeta) throws ExecutionException, InterruptedException {
        String id = pontoColetaService.criar(pontoColeta);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PontoColeta> buscarPorId(@PathVariable String id) throws ExecutionException, InterruptedException {
        PontoColeta ponto = pontoColetaService.buscarPorId(id);
        if (ponto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ponto);
    }

    @GetMapping
    public ResponseEntity<List<PontoColeta>> listarTodos() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(pontoColetaService.listarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable String id, @RequestBody PontoColeta pontoColeta) throws ExecutionException, InterruptedException {
        pontoColetaService.atualizar(id, pontoColeta);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) throws ExecutionException, InterruptedException {
        pontoColetaService.deletar(id);
        return ResponseEntity.ok().build();
    }
}
