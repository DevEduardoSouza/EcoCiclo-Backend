package com.ecociclo.avaliacao.service;

import com.ecociclo.avaliacao.model.Avaliacao;
import com.ecociclo.avaliacao.repository.AvaliacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository repository;

    public AvaliacaoService(AvaliacaoRepository repository) {
        this.repository = repository;
    }

    public String criar(Avaliacao avaliacao) throws ExecutionException, InterruptedException {
        validar(avaliacao);
        return repository.salvar(avaliacao);
    }

    public Avaliacao buscarPorId(String id) throws ExecutionException, InterruptedException {
        return repository.buscarPorId(id);
    }

    public List<Avaliacao> listarTodos() throws ExecutionException, InterruptedException {
        return repository.listarTodos();
    }

    public void atualizar(String id, Avaliacao avaliacao) throws ExecutionException, InterruptedException {
        if (repository.buscarPorId(id) == null) {
            throw new IllegalArgumentException("Avaliacao nao encontrada.");
        }

        validar(avaliacao);
        repository.atualizar(id, avaliacao);
    }

    public void deletar(String id) throws ExecutionException, InterruptedException {
        if (repository.buscarPorId(id) == null) {
            throw new IllegalArgumentException("Avaliacao nao encontrada.");
        }

        repository.deletar(id);
    }

    private void validar(Avaliacao avaliacao) {
        if (avaliacao.getTipo() == null || avaliacao.getTipo().isBlank()) {
            throw new IllegalArgumentException("Campo 'tipo' e obrigatorio.");
        }
        if (avaliacao.getColetor() == null || avaliacao.getColetor().isBlank()) {
            throw new IllegalArgumentException("Campo 'coletor' e obrigatorio.");
        }
        if (avaliacao.getData() == null || avaliacao.getData().isBlank()) {
            throw new IllegalArgumentException("Campo 'data' e obrigatorio.");
        }
    }
}
