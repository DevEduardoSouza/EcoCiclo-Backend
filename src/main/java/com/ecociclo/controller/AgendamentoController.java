package com.ecociclo.controller;

import com.ecociclo.model.Agendamento;
import com.ecociclo.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @PostMapping
    public ResponseEntity<String> criar(@RequestBody Agendamento agendamento) throws ExecutionException, InterruptedException {
        String id = agendamentoService.criar(agendamento);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> buscarPorId(@PathVariable String id) throws ExecutionException, InterruptedException {
        Agendamento agendamento = agendamentoService.buscarPorId(id);
        if (agendamento == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(agendamento);
    }

    @GetMapping
    public ResponseEntity<List<Agendamento>> listarTodos() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(agendamentoService.listarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable String id, @RequestBody Agendamento agendamento) throws ExecutionException, InterruptedException {
        agendamentoService.atualizar(id, agendamento);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) throws ExecutionException, InterruptedException {
        agendamentoService.deletar(id);
        return ResponseEntity.ok().build();
    }
}
