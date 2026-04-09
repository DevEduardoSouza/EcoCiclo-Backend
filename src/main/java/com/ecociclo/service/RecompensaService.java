package com.ecociclo.service;

import com.ecociclo.model.Recompensa;
import com.ecociclo.repository.RecompensaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class RecompensaService {

    @Autowired
    private RecompensaRepository recompensaRepository;

    public String criar(Recompensa recompensa) throws ExecutionException, InterruptedException {
        return recompensaRepository.salvar(recompensa);
    }

    public Recompensa buscarPorId(String id) throws ExecutionException, InterruptedException {
        return recompensaRepository.buscarPorId(id);
    }

    public List<Recompensa> listarTodos() throws ExecutionException, InterruptedException {
        return recompensaRepository.listarTodos();
    }

    public void atualizar(String id, Recompensa recompensa) throws ExecutionException, InterruptedException {
        recompensaRepository.atualizar(id, recompensa);
    }

    public void deletar(String id) throws ExecutionException, InterruptedException {
        recompensaRepository.deletar(id);
    }
}
