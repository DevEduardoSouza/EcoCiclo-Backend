package com.ecociclo.controller;

import org.springframework.web.bind.annotation.*;

import com.ecociclo.model.Recompensa;
import com.ecociclo.service.RecompensaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/recompensas")
public class RecompensaController {

    @Autowired
    private RecompensaService recompensaService;

    public RecompensaController(RecompensaService recompensaService) {
        this.recompensaService = recompensaService;
    }

    // POST /api/recompensas - Apenas ADMIN ou ASSOCIACAO
    @PostMapping("/recompensas")
    public ResponseEntity<?> criar(@RequestBody Recompensa recompensa) throws ExecutionException, InterruptedException {
        try {
            String id = recompensaService.criar(recompensa);
            return ResponseEntity.ok(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // GET /api/recompensas
    @GetMapping("/recompensas")
    public ResponseEntity<?> listar(@RequestParam(value = "disponivel", required = false) Boolean disponivel) {
        // Se disponivel == true, filtra no repository. Se não, traz todas.
        return ResponseEntity.ok().build();
    }

    // DELETE /api/recompensas/{id} - Soft Delete (Apenas altera a flag de disponibilidade)
    @DeleteMapping("/recompensas/{id}")
    public ResponseEntity<?> softDelete(@PathVariable String id) {
        // Busca a recompensa, seta disponivel = false, e atualiza.
        return ResponseEntity.ok().body("Recompensa desativada.");
    }

    // POST /api/recompensas/{id}/resgatar - Apenas DOADOR
    @PostMapping("/recompensas/{id}/resgatar")
    public ResponseEntity<?> resgatar(@PathVariable String id, @RequestBody Map<String, String> body) {
        String usuarioId = body.get("usuarioId");
        try {
            String resgateId = recompensaService.resgatarRecompensa(id, usuarioId);
            return ResponseEntity.ok().body(Map.of("resgateId", resgateId, "status", "Sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Retorna 400 se faltar pontos/estoque
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno no servidor.");
        }
    }

    // GET /api/usuarios/{id}/resgates - Histórico do Usuário
    @GetMapping("/usuarios/{id}/resgates")
    public ResponseEntity<?> historicoResgatedoUsuario(@PathVariable String id) {
        // Chama resgateRepository.listarPorUsuario(id)
        return ResponseEntity.ok().build();
    }
}