package com.ecociclo.controller;

import com.ecociclo.model.Chat;
import com.ecociclo.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

// TODO: Implementação será discutida com a equipe
@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private ChatService service;

    @PostMapping
    public ResponseEntity<?> novoChat(@RequestBody Chat chat) throws ExecutionException, InterruptedException {
        try {
            // Verifica antes de criar para saber se é novo ou existente
            Optional<Chat> existente = service.buscarPorParticipantes(chat.getParticipantesId());

            if (existente.isPresent()) {
                return ResponseEntity.ok(Map.of(
                        "id", existente.get().getId(),
                        "existente", true   // frontend pode usar isso para navegar direto ao chat
                ));
            }

            String id = service.novoChat(chat);
            return ResponseEntity.ok(Map.of("id", id, "existente", false));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<?> buscarChat(@PathVariable String chatId) throws ExecutionException, InterruptedException {
        return service.buscarChat(chatId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/chats/usuario/{usuarioId}
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listarChatsDoUsuario(@PathVariable String usuarioId) throws ExecutionException, InterruptedException {
        try {
            return ResponseEntity.ok(service.listarChatsDoUsuario(usuarioId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }
}
