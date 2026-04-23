package com.ecociclo.service;

import com.ecociclo.model.TipoUsuario;
import com.ecociclo.model.Usuario;
import com.ecociclo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public String criar(Usuario usuario) throws ExecutionException, InterruptedException {
        validar(usuario, null);
        return usuarioRepository.salvar(usuario);
    }

    public Usuario buscarPorId(String id) throws ExecutionException, InterruptedException {
        return usuarioRepository.buscarPorId(id);
    }

    public Usuario buscarPorFirebaseUid(String firebaseUid) throws ExecutionException, InterruptedException {
        return usuarioRepository.buscarPorFirebaseUid(firebaseUid);
    }

    public List<Usuario> listarTodos() throws ExecutionException, InterruptedException {
        return usuarioRepository.listarTodos();
    }

    public List<Usuario> listarPorFiltros(TipoUsuario tipo, String associacaoId)
            throws ExecutionException, InterruptedException {
        return usuarioRepository.listarPorFiltros(tipo, associacaoId);
    }

    public void atualizar(String id, Usuario usuario) throws ExecutionException, InterruptedException {
        validar(usuario, id);
        usuarioRepository.atualizar(id, usuario);
    }

    public void deletar(String id) throws ExecutionException, InterruptedException {
        usuarioRepository.deletar(id);
    }

    // Valida regras de negócio do Usuario. Lança IllegalArgumentException com mensagem amigável.
    // idExistente é o ID do documento sendo atualizado (null quando criando) — usado pra permitir
    // que o próprio usuário mantenha seu email/uid sem conflitar com ele mesmo.
    private void validar(Usuario usuario, String idExistente) throws ExecutionException, InterruptedException {
        if (usuario.getTipo() == null) {
            throw new IllegalArgumentException("Campo 'tipo' é obrigatório (ADMIN, ASSOCIACAO, DOADOR ou RECEPTOR).");
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("Campo 'email' é obrigatório.");
        }
        if (usuario.getNome() == null || usuario.getNome().isBlank()) {
            throw new IllegalArgumentException("Campo 'nome' é obrigatório.");
        }

        if (usuario.getTipo() == TipoUsuario.RECEPTOR) {
            if (usuario.getAssociacaoId() == null || usuario.getAssociacaoId().isBlank()) {
                throw new IllegalArgumentException("RECEPTOR exige 'associacaoId'.");
            }
            Usuario associacao = usuarioRepository.buscarPorId(usuario.getAssociacaoId());
            if (associacao == null || associacao.getTipo() != TipoUsuario.ASSOCIACAO) {
                throw new IllegalArgumentException(
                        "associacaoId '" + usuario.getAssociacaoId() + "' não aponta para um usuário do tipo ASSOCIACAO.");
            }
        } else {
            if (usuario.getAssociacaoId() != null && !usuario.getAssociacaoId().isBlank()) {
                throw new IllegalArgumentException(
                        "Apenas RECEPTOR pode ter 'associacaoId'. Tipo atual: " + usuario.getTipo() + ".");
            }
        }

        Usuario existente = usuarioRepository.buscarPorEmail(usuario.getEmail());
        if (existente != null && !Objects.equals(existente.getId(), idExistente)) {
            throw new IllegalArgumentException("Email '" + usuario.getEmail() + "' já está em uso.");
        }

        if (usuario.getFirebaseUid() != null && !usuario.getFirebaseUid().isBlank()) {
            Usuario porUid = usuarioRepository.buscarPorFirebaseUid(usuario.getFirebaseUid());
            if (porUid != null && !Objects.equals(porUid.getId(), idExistente)) {
                throw new IllegalArgumentException("firebaseUid já vinculado a outro usuário.");
            }
        }
    }
}
