package com.ecociclo.recompensa.service;

import org.springframework.stereotype.Service;

import com.ecociclo.recompensa.model.Recompensa;
import com.ecociclo.resgate.model.Resgate;
import com.ecociclo.usuario.model.Usuario;
import com.ecociclo.recompensa.repository.RecompensaRepository;
import com.ecociclo.resgate.repository.ResgateRepository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

import java.util.List;
import java.time.LocalDateTime;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

@Service
public class RecompensaService {

    private static final String STATUS_PENDENTE = "PENDENTE";
    private static final String STATUS_ENTREGUE = "ENTREGUE";
    private static final String STATUS_CANCELADO = "CANCELADO";

    private final RecompensaRepository recompensaRepository;
    private final ResgateRepository resgateRepository;
    private final Firestore firestore; // Injetado para gerenciar a transacao do SDK do Firebase

    public RecompensaService(RecompensaRepository rRepo, ResgateRepository resRepo, Firestore firestore) {
        this.recompensaRepository = rRepo;
        this.resgateRepository = resRepo;
        this.firestore = firestore;
    }

    public String criar(Recompensa recompensa) throws ExecutionException, InterruptedException {
        validarRecompensa(recompensa);
        return recompensaRepository.criar(recompensa);
    }

    public Recompensa buscarPorId(String id) throws ExecutionException, InterruptedException {
        return recompensaRepository.buscarPorId(id);
    }

    public List<Recompensa> listarTodas() throws ExecutionException, InterruptedException {
        return recompensaRepository.listarTodas();
    }

    public List<Recompensa> listarDisponiveis() throws ExecutionException, InterruptedException {
        return recompensaRepository.listarDisponiveis();
    }

    public void atualizar(String id, Recompensa recompensa) throws ExecutionException, InterruptedException {
        validarRecompensa(recompensa);
        recompensaRepository.atualizar(id, recompensa);
    }

    public void deletar(String id) throws ExecutionException, InterruptedException {
        recompensaRepository.deletar(id);
    }

    // Validacao simples para criacao e edicao
    public void validarRecompensa(Recompensa r) throws ExecutionException, InterruptedException{
        if (r.getNome() == null || r.getNome().isEmpty()) throw new IllegalArgumentException("Nome e obrigatorio.");
        if (r.getCustoPontos() <= 0) throw new IllegalArgumentException("O custo em pontos deve ser maior que zero.");
    }

    // Metodo central do Resgate usando transacao atomica
    public String resgatarRecompensa(String recompensaId, String usuarioId) throws ExecutionException, InterruptedException {
        if (recompensaId == null || recompensaId.isBlank()) {
            throw new RuntimeException("Recompensa e obrigatoria.");
        }
        if (usuarioId == null || usuarioId.isBlank()) {
            throw new RuntimeException("Usuario e obrigatorio.");
        }

        // Referencias dos documentos no Firestore para a transacao
        DocumentReference userRef = firestore.collection("usuarios").document(usuarioId);
        DocumentReference recompensaRef = firestore.collection("recompensas").document(recompensaId);
        DocumentReference resgateRef = firestore.collection("resgates").document(); // Gera ID automatico

        try {
            // Executa a transacao
            firestore.runTransaction(transaction -> {
            // 1. Ler os dados atuais (Regra do Firestore: Leituras SEMPRE antes das escritas)
            DocumentSnapshot docUsuario = transaction.get(userRef).get();
            Usuario usuario = docUsuario.toObject(Usuario.class);
            DocumentSnapshot docRecompensa = transaction.get(recompensaRef).get();
            Recompensa recompensa = docRecompensa.toObject(Recompensa.class);

            if (usuario == null) throw new RuntimeException("Usuario nao encontrado.");
            if (recompensa == null || !recompensa.isDisponivel()) throw new RuntimeException("Recompensa indisponivel.");
            
            // 2. Validar estoque disponivel: estoque real menos itens ja bloqueados
            if (recompensa.getEstoque() != null) {
                int bloqueados = Math.max(recompensa.getBloqueados(), 0);
                int estoqueDisponivel = recompensa.getEstoque() - bloqueados;

                if (estoqueDisponivel <= 0) {
                    throw new RuntimeException("Estoque esgotado.");
                }
            }

            // 3. Validar Saldo de Pontos
            if (usuario.getPontuacao() < recompensa.getCustoPontos()) {
                throw new RuntimeException("Pontuacao insuficiente.");
            }

            // 4. Aplicar as alteracoes na transacao
            // Debitar pontos do usuario
            int novaPontuacao = usuario.getPontuacao() - recompensa.getCustoPontos();
            transaction.update(userRef, "pontuacao", novaPontuacao);

            // Bloquear estoque se nao for ilimitado. O abatimento real acontece na entrega.
            if (recompensa.getEstoque() != null) {
                int novosBloqueados = Math.max(recompensa.getBloqueados(), 0) + 1;
                transaction.update(recompensaRef, "bloqueados", novosBloqueados);
            }

            // Criar o documento de Resgate
            Resgate novoResgate = new Resgate(
                resgateRef.getId(),
                recompensaId,
                usuarioId,
                LocalDateTime.now().toString(),
                recompensa.getCustoPontos(),
                STATUS_PENDENTE
            );
            transaction.set(resgateRef, novoResgate);

            return null;
            }).get(); // Garante a execucao sincrona do bloco da transacao
        } catch (ExecutionException e) {
            throw erroDaTransacao(e, "Erro ao resgatar recompensa.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }

        return resgateRef.getId();
    }

    public void confirmarEntrega(String resgateId) throws ExecutionException, InterruptedException {
        if (resgateId == null || resgateId.isBlank()) {
            throw new RuntimeException("Resgate e obrigatorio.");
        }

        DocumentReference resgateRef = firestore.collection("resgates").document(resgateId);

        try {
            firestore.runTransaction(transaction -> {
            DocumentSnapshot docResgate = transaction.get(resgateRef).get();
            Resgate resgate = docResgate.toObject(Resgate.class);

            if (resgate == null) throw new RuntimeException("Resgate nao encontrado.");
            if (!STATUS_PENDENTE.equals(resgate.getStatus())) {
                throw new RuntimeException("Apenas resgates pendentes podem ser entregues.");
            }
            if (resgate.getRecompensaId() == null || resgate.getRecompensaId().isBlank()) {
                throw new RuntimeException("Resgate sem recompensa vinculada.");
            }

            DocumentReference recompensaRef = firestore.collection("recompensas").document(resgate.getRecompensaId());
            DocumentSnapshot docRecompensa = transaction.get(recompensaRef).get();
            Recompensa recompensa = docRecompensa.toObject(Recompensa.class);

            if (recompensa == null) throw new RuntimeException("Recompensa nao encontrada.");

            if (recompensa.getEstoque() != null) {
                int bloqueados = Math.max(recompensa.getBloqueados(), 0);

                if (bloqueados <= 0) {
                    throw new RuntimeException("Reserva de estoque nao encontrada para este resgate.");
                }
                if (recompensa.getEstoque() <= 0) {
                    throw new RuntimeException("Estoque inconsistente para confirmar entrega.");
                }

                transaction.update(recompensaRef, "estoque", recompensa.getEstoque() - 1);
                transaction.update(recompensaRef, "bloqueados", bloqueados - 1);
            }

            transaction.update(resgateRef, "status", STATUS_ENTREGUE);

            return null;
            }).get();
        } catch (ExecutionException e) {
            throw erroDaTransacao(e, "Erro ao confirmar entrega.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    public void estornarResgate(String resgateId) throws ExecutionException, InterruptedException {
        if (resgateId == null || resgateId.isBlank()) {
            throw new RuntimeException("Resgate e obrigatorio.");
        }

        DocumentReference resgateRef = firestore.collection("resgates").document(resgateId);

        try {
            firestore.runTransaction(transaction -> {
            DocumentSnapshot docResgate = transaction.get(resgateRef).get();
            Resgate resgate = docResgate.toObject(Resgate.class);

            if (resgate == null) throw new RuntimeException("Resgate nao encontrado.");
            if (!STATUS_PENDENTE.equals(resgate.getStatus())) {
                throw new RuntimeException("Apenas resgates pendentes podem ser estornados.");
            }
            if (resgate.getUsuarioId() == null || resgate.getUsuarioId().isBlank()) {
                throw new RuntimeException("Resgate sem usuario vinculado.");
            }
            if (resgate.getRecompensaId() == null || resgate.getRecompensaId().isBlank()) {
                throw new RuntimeException("Resgate sem recompensa vinculada.");
            }

            DocumentReference userRef = firestore.collection("usuarios").document(resgate.getUsuarioId());
            DocumentReference recompensaRef = firestore.collection("recompensas").document(resgate.getRecompensaId());

            DocumentSnapshot docUsuario = transaction.get(userRef).get();
            Usuario usuario = docUsuario.toObject(Usuario.class);
            DocumentSnapshot docRecompensa = transaction.get(recompensaRef).get();
            Recompensa recompensa = docRecompensa.toObject(Recompensa.class);

            if (usuario == null) throw new RuntimeException("Usuario nao encontrado.");
            if (recompensa == null) throw new RuntimeException("Recompensa nao encontrada.");

            if (recompensa.getEstoque() != null) {
                int bloqueados = Math.max(recompensa.getBloqueados(), 0);

                if (bloqueados <= 0) {
                    throw new RuntimeException("Reserva de estoque nao encontrada para este resgate.");
                }

                transaction.update(recompensaRef, "bloqueados", bloqueados - 1);
            }

            transaction.update(userRef, "pontuacao", usuario.getPontuacao() + resgate.getPontosGastos());
            transaction.update(resgateRef, "status", STATUS_CANCELADO);

            return null;
            }).get();
        } catch (ExecutionException e) {
            throw erroDaTransacao(e, "Erro ao estornar resgate.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    // Metodo publico para o -- Agendamento -- chamar
    public void creditarPontos(String usuarioId, int quantidade, String origemId) throws ExecutionException, InterruptedException {
        DocumentReference userRef = firestore.collection("usuarios").document(usuarioId);
        
        try {
            firestore.runTransaction(transaction -> {
            DocumentSnapshot docUsuario = transaction.get(userRef).get();
            Usuario usuario = docUsuario.toObject(Usuario.class);
            if (usuario == null) throw new RuntimeException("Usuario nao encontrado.");
            
            int novaPontuacao = usuario.getPontuacao() + quantidade;
            transaction.update(userRef, "pontuacao", novaPontuacao);
            
            return null;
            }).get();
        } catch (ExecutionException e) {
            throw erroDaTransacao(e, "Erro ao creditar pontos.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    private RuntimeException erroDaTransacao(ExecutionException e, String mensagemPadrao) {
        Throwable causa = e.getCause();
        while (causa instanceof CompletionException && causa.getCause() != null) {
            causa = causa.getCause();
        }

        if (causa instanceof RuntimeException && causa.getMessage() != null && !causa.getMessage().isBlank()) {
            return new RuntimeException(causa.getMessage(), causa);
        }

        if (causa != null && causa.getMessage() != null && !causa.getMessage().isBlank()) {
            return new RuntimeException(causa.getMessage(), causa);
        }

        return new RuntimeException(mensagemPadrao, e);
    }
}
