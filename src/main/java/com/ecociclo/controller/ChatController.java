package com.ecociclo.controller;

import com.ecociclo.model.Chat;
import com.ecociclo.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping
    public ResponseEntity<String> criar(@RequestBody Chat chat) throws ExecutionException, InterruptedException {
        String id = chatService.criar(chat);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chat> buscarPorId(@PathVariable String id) throws ExecutionException, InterruptedException {
        Chat chat = chatService.buscarPorId(id);
        if (chat == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(chat);
    }

    @GetMapping
    public ResponseEntity<List<Chat>> listarTodos() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(chatService.listarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable String id, @RequestBody Chat chat) throws ExecutionException, InterruptedException {
        chatService.atualizar(id, chat);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) throws ExecutionException, InterruptedException {
        chatService.deletar(id);
        return ResponseEntity.ok().build();
    }
}
