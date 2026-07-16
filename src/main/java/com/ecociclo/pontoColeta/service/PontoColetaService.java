package com.ecociclo.pontoColeta.service;

import com.ecociclo.pontoColeta.model.Endereco;
import com.ecociclo.pontoColeta.model.PontoColeta;
import com.ecociclo.pontoColeta.repository.PontoColetaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class PontoColetaService {

    private final PontoColetaRepository repository;

    public PontoColetaService(PontoColetaRepository repository) {
        this.repository = repository;
    }

    public PontoColeta salvar(PontoColeta pontoColeta) {
        try {
            validar(pontoColeta);
            pontoColeta.setId(null);
            pontoColeta.setAtivo(true);
            normalizar(pontoColeta);
            repository.salvar(pontoColeta);
            return pontoColeta;
        } catch (ExecutionException e) {
            throw new RuntimeException("Erro ao salvar ponto de coleta", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operacao interrompida ao salvar ponto de coleta", e);
        }
    }

    public List<PontoColeta> listarTodos() {
        try {
            return repository.listarTodos();
        } catch (ExecutionException e) {
            throw new RuntimeException("Erro ao listar pontos de coleta", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operacao interrompida ao listar pontos de coleta", e);
        }
    }

    public PontoColeta buscarPorId(String id) {
        try {
            validarId(id);
            return repository.buscarPorId(id);
        } catch (ExecutionException e) {
            throw new RuntimeException("Erro ao buscar ponto de coleta", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operacao interrompida ao buscar ponto de coleta", e);
        }
    }

    public PontoColeta atualizar(String id, PontoColeta pontoColeta) {
        try {
            validarId(id);
            validar(pontoColeta);

            PontoColeta existente = repository.buscarPorId(id);
            if (existente == null) {
                throw new IllegalArgumentException("Ponto de coleta nao encontrado.");
            }

            PontoColeta atualizado = new PontoColeta(
                    id,
                    pontoColeta.getNome().trim(),
                    pontoColeta.getResponsavel().trim(),
                    pontoColeta.getTelefone().trim(),
                    pontoColeta.getEmail().trim(),
                    normalizarEndereco(pontoColeta.getEndereco()),
                    pontoColeta.getAtivo() == null ? existente.getAtivo() : pontoColeta.getAtivo()
            );

            repository.atualizar(id, atualizado);
            return atualizado;
        } catch (ExecutionException e) {
            throw new RuntimeException("Erro ao atualizar ponto de coleta", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operacao interrompida ao atualizar ponto de coleta", e);
        }
    }

    public PontoColeta desativar(String id) {
        return atualizarAtivo(id, false);
    }

    public PontoColeta ativar(String id) {
        return atualizarAtivo(id, true);
    }

    public void deletar(String id) {
        try {
            validarId(id);
            if (repository.buscarPorId(id) == null) {
                throw new IllegalArgumentException("Ponto de coleta nao encontrado.");
            }

            repository.deletar(id);
        } catch (ExecutionException e) {
            throw new RuntimeException("Erro ao deletar ponto de coleta", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operacao interrompida ao deletar ponto de coleta", e);
        }
    }

    private PontoColeta atualizarAtivo(String id, boolean ativo) {
        try {
            validarId(id);
            PontoColeta ponto = repository.buscarPorId(id);
            if (ponto == null) {
                throw new IllegalArgumentException("Ponto de coleta nao encontrado.");
            }

            ponto.setAtivo(ativo);
            repository.atualizar(id, ponto);
            return ponto;
        } catch (ExecutionException e) {
            throw new RuntimeException("Erro ao atualizar status do ponto de coleta", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operacao interrompida ao atualizar status do ponto de coleta", e);
        }
    }

    private void validar(PontoColeta pontoColeta) {
        if (pontoColeta == null) {
            throw new IllegalArgumentException("Dados do ponto de coleta sao obrigatorios.");
        }

        validarTexto(pontoColeta.getNome(), "nome");
        validarTexto(pontoColeta.getResponsavel(), "responsavel");
        validarTexto(pontoColeta.getTelefone(), "telefone");
        validarTexto(pontoColeta.getEmail(), "email");

        if (!pontoColeta.getEmail().contains("@")) {
            throw new IllegalArgumentException("Campo 'email' deve ser valido.");
        }

        Endereco endereco = pontoColeta.getEndereco();
        if (endereco == null) {
            throw new IllegalArgumentException("Campo 'endereco' e obrigatorio.");
        }

        validarTexto(endereco.getLogradouro(), "endereco.logradouro");
        validarTexto(endereco.getBairro(), "endereco.bairro");
        validarTexto(endereco.getCidade(), "endereco.cidade");
        validarTexto(endereco.getEstado(), "endereco.estado");
        validarTexto(endereco.getCep(), "endereco.cep");
    }

    private void validarId(String id) {
        validarTexto(id, "id");
    }

    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Campo '" + campo + "' e obrigatorio.");
        }
    }

    private void normalizar(PontoColeta pontoColeta) {
        pontoColeta.setNome(pontoColeta.getNome().trim());
        pontoColeta.setResponsavel(pontoColeta.getResponsavel().trim());
        pontoColeta.setTelefone(pontoColeta.getTelefone().trim());
        pontoColeta.setEmail(pontoColeta.getEmail().trim());
        pontoColeta.setEndereco(normalizarEndereco(pontoColeta.getEndereco()));
    }

    private Endereco normalizarEndereco(Endereco endereco) {
        return new Endereco(
                endereco.getLogradouro().trim(),
                endereco.getBairro().trim(),
                endereco.getCidade().trim(),
                endereco.getEstado().trim(),
                endereco.getCep().trim()
        );
    }
}
