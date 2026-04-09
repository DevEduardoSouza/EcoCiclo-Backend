package com.ecociclo.service;

import com.ecociclo.model.PontoColeta;
import com.ecociclo.repository.PontoColetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class PontoColetaService {

    @Autowired
    private PontoColetaRepository pontoColetaRepository;

    public String criar(PontoColeta pontoColeta) throws ExecutionException, InterruptedException {
        return pontoColetaRepository.salvar(pontoColeta);
    }

    public PontoColeta buscarPorId(String id) throws ExecutionException, InterruptedException {
        return pontoColetaRepository.buscarPorId(id);
    }

    public List<PontoColeta> listarTodos() throws ExecutionException, InterruptedException {
        return pontoColetaRepository.listarTodos();
    }

    public void atualizar(String id, PontoColeta pontoColeta) throws ExecutionException, InterruptedException {
        pontoColetaRepository.atualizar(id, pontoColeta);
    }

    public void deletar(String id) throws ExecutionException, InterruptedException {
        pontoColetaRepository.deletar(id);
    }
}
