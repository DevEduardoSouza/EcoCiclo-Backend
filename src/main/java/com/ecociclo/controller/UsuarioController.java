package com.ecociclo.controller;

import com.ecociclo.model.TipoUsuario;
import com.ecociclo.model.Usuario;
import com.ecociclo.service.UsuarioService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Usuario usuario) throws ExecutionException, InterruptedException {
        try {
            String id = usuarioService.criar(usuario);
            return ResponseEntity.ok(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // Retorna o usuário correspondente ao token Firebase enviado no header Authorization.
    // Precisa vir antes de /{id} para não ser interpretado como um ID de documento.
    @GetMapping("/me")
    public ResponseEntity<?> buscarUsuarioLogado(HttpServletRequest request)
            throws ExecutionException, InterruptedException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("erro", "Token de autenticação não fornecido."));
        }

        String token = authHeader.substring(7);
        String uid;
        try {
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(token);
            uid = decoded.getUid();
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body(Map.of("erro", "Token inválido ou expirado."));
        }

        Usuario usuario = usuarioService.buscarPorFirebaseUid(uid);
        if (usuario == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "erro", "Usuário autenticado mas ainda não cadastrado no sistema.",
                    "firebaseUid", uid));
        }
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable String id)
            throws ExecutionException, InterruptedException {
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }

    // GET /api/usuarios?tipo=RECEPTOR&associacaoId=xyz — ambos os filtros são opcionais.
    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String associacaoId)
            throws ExecutionException, InterruptedException {
        TipoUsuario tipoFiltro = null;
        if (tipo != null && !tipo.isBlank()) {
            try {
                tipoFiltro = TipoUsuario.valueOf(tipo.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "erro", "Tipo inválido: '" + tipo + "'. Valores aceitos: ADMIN, ASSOCIACAO, DOADOR, RECEPTOR."));
            }
        }
        List<Usuario> usuarios = usuarioService.listarPorFiltros(tipoFiltro, associacaoId);
        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable String id, @RequestBody Usuario usuario)
            throws ExecutionException, InterruptedException {
        try {
            usuarioService.atualizar(id, usuario);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id)
            throws ExecutionException, InterruptedException {
        usuarioService.deletar(id);
        return ResponseEntity.ok().build();
    }
}
