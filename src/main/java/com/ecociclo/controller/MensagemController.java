package com.ecociclo.controller;

import com.ecociclo.model.Mensagem;
import com.ecociclo.service.MensagemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/chats/{chatId}/mensagens")
public class MensagemController {

    @Autowired
    private MensagemService mensagemService;

    // POST /api/chats/{chatId}/mensagens
    @PostMapping
    public ResponseEntity<?> enviar(
            @PathVariable String chatId,
            @RequestBody Mensagem mensagem) throws ExecutionException, InterruptedException {
        try {
            String id = mensagemService.enviar(chatId, mensagem);
            return ResponseEntity.ok(Map.of("id", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // GET /api/chats/{chatId}/mensagens?limite=30&cursor={mensagemId}
    //
    // cursor = ID da mensagem mais antiga já exibida.
    // Omitir cursor = retorna a página mais recente.
    @GetMapping
    public ResponseEntity<?> listar(
            @PathVariable String chatId,
            @RequestParam(required = false) Integer limite,
            @RequestParam(required = false) String cursor) throws ExecutionException, InterruptedException {
        try {
            List<Mensagem> mensagens = mensagemService.listar(chatId, limite, cursor);
            return ResponseEntity.ok(mensagens);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // DELETE /api/chats/{chatId}/mensagens/{mensagemId}
    @DeleteMapping("/{mensagemId}")
    public ResponseEntity<?> deletar(
            @PathVariable String chatId,
            @PathVariable String mensagemId) throws ExecutionException, InterruptedException {
        mensagemService.deletar(chatId, mensagemId, null);
        return ResponseEntity.ok().build();
    }

    // PATCH /api/chats/{chatId}/mensagens/{mensagemId}/lida
    @PatchMapping("/{mensagemId}/lida")
    public ResponseEntity<?> marcarComoLida(
            @PathVariable String chatId,
            @PathVariable String mensagemId) throws ExecutionException, InterruptedException {
        try {
            mensagemService.marcarComoLida(chatId, mensagemId, null);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // PATCH /api/chats/{chatId}/mensagens/lidas?usuarioId={usuarioId}
    @PatchMapping("/lidas")
    public ResponseEntity<?> marcarTodasComoLidas(
            @PathVariable String chatId,
            @RequestParam String usuarioId) throws ExecutionException, InterruptedException {
        try {
            mensagemService.marcarTodasComoLidas(chatId, usuarioId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

}