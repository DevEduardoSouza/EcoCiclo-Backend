package com.ecociclo.controller;

import com.ecociclo.model.Recompensa;
import com.ecociclo.service.RecompensaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/recompensas")
public class RecompensaController {

    @Autowired
    private RecompensaService recompensaService;

    @PostMapping
    public ResponseEntity<String> criar(@RequestBody Recompensa recompensa) throws ExecutionException, InterruptedException {
        String id = recompensaService.criar(recompensa);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recompensa> buscarPorId(@PathVariable String id) throws ExecutionException, InterruptedException {
        Recompensa recompensa = recompensaService.buscarPorId(id);
        if (recompensa == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recompensa);
    }

    @GetMapping
    public ResponseEntity<List<Recompensa>> listarTodos() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(recompensaService.listarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable String id, @RequestBody Recompensa recompensa) throws ExecutionException, InterruptedException {
        recompensaService.atualizar(id, recompensa);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) throws ExecutionException, InterruptedException {
        recompensaService.deletar(id);
        return ResponseEntity.ok().build();
    }
}
