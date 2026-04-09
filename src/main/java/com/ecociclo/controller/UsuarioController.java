package com.ecociclo.controller;

import com.ecociclo.model.Usuario;
import com.ecociclo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<String> criar(@RequestBody Usuario usuario) throws ExecutionException, InterruptedException {
        String id = usuarioService.criar(usuario);
        return ResponseEntity.ok(id);f
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable String id) throws ExecutionException, InterruptedException {
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable String id, @RequestBody Usuario usuario) throws ExecutionException, InterruptedException {
        usuarioService.atualizar(id, usuario);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) throws ExecutionException, InterruptedException {
        usuarioService.deletar(id);
        return ResponseEntity.ok().build();
    }
}
