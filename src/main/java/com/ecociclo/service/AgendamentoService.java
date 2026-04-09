package com.ecociclo.service;

import com.ecociclo.model.Agendamento;
import com.ecociclo.repository.AgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    public String criar(Agendamento agendamento) throws ExecutionException, InterruptedException {
        return agendamentoRepository.salvar(agendamento);
    }

    public Agendamento buscarPorId(String id) throws ExecutionException, InterruptedException {
        return agendamentoRepository.buscarPorId(id);
    }

    public List<Agendamento> listarTodos() throws ExecutionException, InterruptedException {
        return agendamentoRepository.listarTodos();
    }

    public void atualizar(String id, Agendamento agendamento) throws ExecutionException, InterruptedException {
        agendamentoRepository.atualizar(id, agendamento);
    }

    public void deletar(String id) throws ExecutionException, InterruptedException {
        agendamentoRepository.deletar(id);
    }
}
