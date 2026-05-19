package com.ecociclo.service;

import com.ecociclo.model.PontoColeta;
import com.ecociclo.repository.PontoColetaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class PontoColetaService{
    private final PontoColetaRepository repository;

    public PontoColetaService(PontoColetaRepository repository){
        this.repository = repository;
    }

    public PontoColeta salvar(PontoColeta pontoColeta){
        try{
            pontoColeta.setAtivo(true);

            String id = repository.salvar(pontoColeta);

            pontoColeta.setId(id);

            return pontoColeta;
        }catch (ExecutionException | InterruptedException e){
            throw new RuntimeException("Erro ao salvar ponto de coleta", e);
        }
    }

    // Listar todos
    public List<PontoColeta> listarTodos() {

        try {
            return repository.listarTodos();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Erro ao listar pontos", e);
        }
    }

    // Buscar por ID
    public PontoColeta buscarPorId(String id) {

        try {

            PontoColeta ponto = repository.buscarPorId(id);

            if (ponto == null) {
                throw new RuntimeException("Ponto não encontrado");
            }

            return ponto;

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Erro ao buscar ponto", e);
        }
    }

    //Atualizar
    public PontoColeta atualizar(String id, PontoColeta pontoColeta) {

        try {

            PontoColeta existente = buscarPorId(id);

            existente.setNome(pontoColeta.getNome());
            existente.setResponsavel(pontoColeta.getResponsavel());
            existente.setTelefone(pontoColeta.getTelefone());
            existente.setEmail(pontoColeta.getEmail());
            existente.setEndereco(pontoColeta.getEndereco());
            existente.setBairro(pontoColeta.getBairro());
            existente.setCidade(pontoColeta.getCidade());
            existente.setEstado(pontoColeta.getEstado());
            existente.setCep(pontoColeta.getCep());

            repository.atualizar(id, existente);

            return existente;

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Erro ao atualizar ponto", e);
        }
    }

    //Desativart
    public PontoColeta desativar(String id){
        try{
            PontoColeta ponto = buscarPorId(id);
            ponto.setAtivo(false);

            repository.atualizar(id, ponto);

            return ponto;
        } catch (ExecutionException | InterruptedException e){
            throw new RuntimeException("Erro ao desativar ponto", e);
        }
    }

    //Ativar
    public PontoColeta ativar(String id){
        try{
            PontoColeta ponto = buscarPorId(id);
            ponto.setAtivo(true);
            repository.atualizar(id, ponto);
            return ponto;
        }catch (ExecutionException | InterruptedException e){
            throw new RuntimeException("Erro ao ativar ponto", e);
        }
    }
       
}