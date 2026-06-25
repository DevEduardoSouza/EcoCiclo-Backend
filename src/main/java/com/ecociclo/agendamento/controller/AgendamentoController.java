package com.ecociclo.agendamento.controller;

import com.ecociclo.agendamento.model.Agendamento;
import com.ecociclo.agendamento.model.StatusAgendamento;
import com.ecociclo.agendamento.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    // POST /api/agendamentos
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Agendamento agendamento) throws ExecutionException, InterruptedException {
        try {
            String id = agendamentoService.criar(agendamento);
            return ResponseEntity.ok(id);
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // GET /api/agendamentos?doadorId=&receptorId=&status=&dataInicio=&dataFim=
    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) String doadorId,
            @RequestParam(required = false) String receptorId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim)
            throws ExecutionException, InterruptedException {

        StatusAgendamento statusFiltro = null;
        if (status != null && !status.isBlank()) {
            try {
                statusFiltro = StatusAgendamento.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "erro", "Status inválido: '" + status + "'. Valores aceitos: PENDENTE, CONFIRMADO, CONCLUIDO, CANCELADO."));
            }
        }

        List<Agendamento> lista = agendamentoService.listarPorFiltros(doadorId, receptorId, statusFiltro, dataInicio, dataFim);
        return ResponseEntity.ok(lista);
    }

    // GET /api/agendamentos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable String id) throws ExecutionException, InterruptedException {
        Agendamento ag = agendamentoService.buscarPorId(id);
        if(ag == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ag);
    }

    // PUT /api/agendamentos/{id}/status
    // Body: { "status": "CONFIRMADO" }
    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body)
            throws ExecutionException, InterruptedException {
        try {
            String statusStr = body.get("status");
            if (statusStr == null || statusStr.isBlank())
                return ResponseEntity.badRequest().body(Map.of("erro", "Campo 'status' é obrigatório no body."));

            StatusAgendamento novoStatus;
            try {
                novoStatus = StatusAgendamento.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "erro", "Status inválido: '" + statusStr + "'. Valores aceitos: PENDENTE, CONFIRMADO, CONCLUIDO, CANCELADO."));
            }

            agendamentoService.transicionarStatus(id, novoStatus);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // DELETE /api/agendamento/{id} -> seta CANCELADO, nao deleta fisicamente
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelar(@PathVariable String id) throws ExecutionException, InterruptedException {
        try {
            agendamentoService.deletar(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable String id,
            @RequestBody Agendamento agendamento)
            throws ExecutionException, InterruptedException {

        try {
            agendamentoService.atualizar(id, agendamento);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }




}