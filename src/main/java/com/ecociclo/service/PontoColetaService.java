package com.ecociclo.service;

import com.ecociclo.model.PontoColeta;
import com.ecociclo.repository.PontoColetaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PontoColetaService {

    private final PontoColetaRepository repository;

    public PontoColetaService(PontoColetaRepository repository) {
        this.repository = repository;
    }

    // Cadastrar ponto de coleta
    public PontoColeta salvar(PontoColeta pontoColeta) {
        pontoColeta.setAtivo(true);
        return repository.save(pontoColeta);
    }

    // Listar todos os pontos de coleta
    public List<PontoColeta> listarTodos() {
        return repository.findAll();
    }

    // Buscar ponto por ID
    public PontoColeta buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ponto de coleta não encontrado"));
    }

    // Atualizar ponto de coleta
    public PontoColeta atualizar(Long id, PontoColeta pontoColeta) {

        PontoColeta pontoExistente = buscarPorId(id);

        pontoExistente.setNome(pontoColeta.getNome());
        pontoExistente.setResponsavel(pontoColeta.getResponsavel());
        pontoExistente.setTelefone(pontoColeta.getTelefone());
        pontoExistente.setEmail(pontoColeta.getEmail());
        pontoExistente.setEndereco(pontoColeta.getEndereco());
        pontoExistente.setBairro(pontoColeta.getBairro());
        pontoExistente.setCidade(pontoColeta.getCidade());
        pontoExistente.setEstado(pontoColeta.getEstado());
        pontoExistente.setCep(pontoColeta.getCep());

        return repository.save(pontoExistente);
    }

    // Deletar ponto de coleta
    public void deletar(Long id) {
        PontoColeta ponto = buscarPorId(id);
        repository.delete(ponto);
    }

    // Desativar ponto de coleta
    public PontoColeta desativar(Long id) {
        PontoColeta ponto = buscarPorId(id);
        ponto.setAtivo(false);
        return repository.save(ponto);
    }

    // Ativar ponto de coleta
    public PontoColeta ativar(Long id) {
        PontoColeta ponto = buscarPorId(id);
        ponto.setAtivo(true);
        return repository.save(ponto);
    }
}