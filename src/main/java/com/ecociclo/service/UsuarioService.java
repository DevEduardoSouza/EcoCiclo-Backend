package com.ecociclo.service;

import com.ecociclo.model.Usuario;
import com.ecociclo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public String criar(Usuario usuario) throws ExecutionException, InterruptedException {
        return usuarioRepository.salvar(usuario);
    }

    public Usuario buscarPorId(String id) throws ExecutionException, InterruptedException {
        return usuarioRepository.buscarPorId(id);
    }

    public List<Usuario> listarTodos() throws ExecutionException, InterruptedException {
        return usuarioRepository.listarTodos();
    }

    public void atualizar(String id, Usuario usuario) throws ExecutionException, InterruptedException {
        usuarioRepository.atualizar(id, usuario);
    }

    public void deletar(String id) throws ExecutionException, InterruptedException {
        usuarioRepository.deletar(id);
    }
}
