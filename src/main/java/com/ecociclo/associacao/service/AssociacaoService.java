package com.ecociclo.associacao.service;

import com.ecociclo.associacao.model.Associacao;
import com.ecociclo.associacao.model.StatusAssociacao;
import com.ecociclo.associacao.repository.AssociacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class AssociacaoService {

    private final AssociacaoRepository repository;

    public AssociacaoService(AssociacaoRepository repository) {
        this.repository = repository;
    }

    public String criar(Associacao associacao) throws ExecutionException, InterruptedException {
        validar(associacao);
        if (associacao.getStatus() == null) {
            associacao.setStatus(StatusAssociacao.PENDENTE);
        }

        return repository.salvar(associacao);
    }

    public Associacao buscarPorId(String id) throws ExecutionException, InterruptedException {
        return repository.buscarPorId(id);
    }

    public List<Associacao> listarTodos() throws ExecutionException, InterruptedException {
        return repository.listarTodos();
    }

    public void atualizar(String id, Associacao associacao) throws ExecutionException, InterruptedException {
        if (repository.buscarPorId(id) == null) {
            throw new IllegalArgumentException("Associacao nao encontrada.");
        }

        validar(associacao);
        if (associacao.getStatus() == null) {
            associacao.setStatus(StatusAssociacao.PENDENTE);
        }

        repository.atualizar(id, associacao);
    }

    public void atualizarStatus(String id, StatusAssociacao status) throws ExecutionException, InterruptedException {
        if (status == null) {
            throw new IllegalArgumentException("Campo 'status' e obrigatorio.");
        }

        Associacao associacao = repository.buscarPorId(id);
        if (associacao == null) {
            throw new IllegalArgumentException("Associacao nao encontrada.");
        }

        associacao.setStatus(status);
        repository.atualizar(id, associacao);
    }

    public void deletar(String id) throws ExecutionException, InterruptedException {
        if (repository.buscarPorId(id) == null) {
            throw new IllegalArgumentException("Associacao nao encontrada.");
        }

        repository.deletar(id);
    }

    private void validar(Associacao associacao) {
        if (associacao.getNomeAssociacao() == null || associacao.getNomeAssociacao().isBlank()) {
            throw new IllegalArgumentException("Campo 'nomeAssociacao' e obrigatorio.");
        }
    }
}
