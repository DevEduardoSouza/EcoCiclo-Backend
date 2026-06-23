package com.ecociclo.recompensa.controller;

import org.springframework.web.bind.annotation.*;

import com.ecociclo.recompensa.model.Recompensa;
import com.ecociclo.recompensa.service.RecompensaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import java.util.List;
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
    @PostMapping({"", "/recompensas"})
    public ResponseEntity<?> criar(@RequestBody Recompensa recompensa) throws ExecutionException, InterruptedException {
        try {
            String id = recompensaService.criar(recompensa);
            return ResponseEntity.ok(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // GET /api/recompensas
    @GetMapping({"", "/recompensas"})
    public ResponseEntity<?> listar(@RequestParam(value = "disponivel", required = false) Boolean disponivel)
            throws ExecutionException, InterruptedException {
        List<Recompensa> recompensas = Boolean.TRUE.equals(disponivel)
                ? recompensaService.listarDisponiveis()
                : recompensaService.listarTodas();

        return ResponseEntity.ok(recompensas);
    }

    // GET /api/recompensas/{id}
    @GetMapping({"/{id}", "/recompensas/{id}"})
    public ResponseEntity<?> buscarPorId(@PathVariable String id) throws ExecutionException, InterruptedException {
        Recompensa recompensa = recompensaService.buscarPorId(id);
        if (recompensa == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recompensa);
    }

    // PUT /api/recompensas/{id}
    @PutMapping({"/{id}", "/recompensas/{id}"})
    public ResponseEntity<?> atualizar(@PathVariable String id, @RequestBody Recompensa recompensa)
            throws ExecutionException, InterruptedException {
        try {
            Recompensa existente = recompensaService.buscarPorId(id);
            if (existente == null) {
                return ResponseEntity.notFound().build();
            }

            recompensaService.atualizar(id, recompensa);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // DELETE /api/recompensas/{id} - Soft Delete (Apenas altera a flag de disponibilidade)
    @DeleteMapping({"/{id}", "/recompensas/{id}"})
    public ResponseEntity<?> softDelete(@PathVariable String id) throws ExecutionException, InterruptedException {
        Recompensa recompensa = recompensaService.buscarPorId(id);
        if (recompensa == null) {
            return ResponseEntity.notFound().build();
        }

        recompensa.setDisponivel(false);
        recompensaService.atualizar(id, recompensa);
        return ResponseEntity.ok().body("Recompensa desativada.");
    }

    // POST /api/recompensas/{id}/resgatar - Apenas DOADOR
    @PostMapping({"/{id}/resgatar", "/recompensas/{id}/resgatar"})
    public ResponseEntity<?> resgatar(@PathVariable String id, @RequestBody Map<String, String> body) {
        String usuarioId = body.get("usuarioId");
        try {
            String resgateId = recompensaService.resgatarRecompensa(id, usuarioId);
            return ResponseEntity.ok().body(Map.of("resgateId", resgateId, "status", "PENDENTE"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Retorna 400 se faltar pontos/estoque
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno no servidor.");
        }
    }

    // POST /api/recompensas/resgates/{resgateId}/confirmar-entrega - Apenas ADMIN
    @PostMapping("/resgates/{resgateId}/confirmar-entrega")
    public ResponseEntity<?> confirmarEntrega(@PathVariable String resgateId) {
        try {
            recompensaService.confirmarEntrega(resgateId);
            return ResponseEntity.ok().body(Map.of("resgateId", resgateId, "status", "ENTREGUE"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno no servidor.");
        }
    }

    // POST /api/recompensas/resgates/{resgateId}/estornar - Apenas ADMIN
    @PostMapping("/resgates/{resgateId}/estornar")
    public ResponseEntity<?> estornarResgate(@PathVariable String resgateId) {
        try {
            recompensaService.estornarResgate(resgateId);
            return ResponseEntity.ok().body(Map.of("resgateId", resgateId, "status", "CANCELADO"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno no servidor.");
        }
    }

    // GET /api/usuarios/{id}/resgates - Historico do Usuario
    @GetMapping("/usuarios/{id}/resgates")
    public ResponseEntity<?> historicoResgatedoUsuario(@PathVariable String id) {
        // Chama resgateRepository.listarPorUsuario(id)
        return ResponseEntity.ok().build();
    }
}
