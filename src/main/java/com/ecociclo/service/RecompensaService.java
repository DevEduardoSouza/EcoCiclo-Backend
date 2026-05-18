package com.ecociclo.service;

import org.springframework.stereotype.Service;

import com.ecociclo.model.Recompensa;
import com.ecociclo.model.Resgate;
import com.ecociclo.model.Usuario;
import com.ecociclo.repository.RecompensaRepository;
import com.ecociclo.repository.ResgateRepository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

import java.util.List;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

@Service
public class RecompensaService {

    private final RecompensaRepository recompensaRepository;
    private final ResgateRepository resgateRepository;
    private final Firestore firestore; // Injetado para gerenciar a transação do SDK do Firebase

    public RecompensaService(RecompensaRepository rRepo, ResgateRepository resRepo, Firestore firestore) {
        this.recompensaRepository = rRepo;
        this.resgateRepository = resRepo;
        this.firestore = firestore;
    }

    public String criar(Recompensa recompensa) throws ExecutionException, InterruptedException {
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
        recompensaRepository.atualizar(id, recompensa);
    }

    public void deletar(String id) throws ExecutionException, InterruptedException {
        recompensaRepository.deletar(id);
    }

    // Validação simples para criação e edição
    public void validarRecompensa(Recompensa r) throws ExecutionException, InterruptedException{
        if (r.getNome() == null || r.getNome().isEmpty()) throw new IllegalArgumentException("Nome é obrigatório.");
        if (r.getCustoPontos() <= 0) throw new IllegalArgumentException("O custo em pontos deve ser maior que zero.");
    }

    // Método central do Resgate usando transação atômica
    public String resgatarRecompensa(String recompensaId, String usuarioId) throws ExecutionException, InterruptedException {
        // Referências dos documentos no Firestore para a transação
        DocumentReference userRef = firestore.collection("usuarios").document(usuarioId);
        DocumentReference recompensaRef = firestore.collection("recompensas").document(recompensaId);
        DocumentReference resgateRef = firestore.collection("resgates").document(); // Gera ID automático

        // Executa a transação
        firestore.runTransaction(transaction -> {
            // 1. Ler os dados atuais (Regra do Firestore: Leituras SEMPRE antes das escritas)
            DocumentSnapshot docUsuario = transaction.get(userRef).get();
            Usuario usuario = docUsuario.toObject(Usuario.class);
            DocumentSnapshot docRecompensa = transaction.get(recompensaRef).get();
            Recompensa recompensa = docRecompensa.toObject(Recompensa.class);

            if (usuario == null) throw new RuntimeException("Usuário não encontrado.");
            if (recompensa == null || !recompensa.isDisponivel()) throw new RuntimeException("Recompensa indisponível.");
            
            // 2. Validar Estoque
            if (recompensa.getEstoque() != null && recompensa.getEstoque() <= 0) {
                throw new RuntimeException("Estoque esgotado.");
            }

            // 3. Validar Saldo de Pontos
            if (usuario.getPontuacao() < recompensa.getCustoPontos()) {
                throw new RuntimeException("Pontuação insuficiente.");
            }

            // 4. Aplicar as alterações na transação
            // Debitar pontos do usuário
            int novaPontuacao = usuario.getPontuacao() - recompensa.getCustoPontos();
            transaction.update(userRef, "pontuacao", novaPontuacao);

            // Decrementar estoque se não for ilimitado
            if (recompensa.getEstoque() != null) {
                transaction.update(recompensaRef, "estoque", recompensa.getEstoque() - 1);
            }

            // Criar o documento de Resgate
            Resgate novoResgate = new Resgate(
                resgateRef.getId(),
                recompensaId,
                usuarioId,
                LocalDateTime.now(),
                recompensa.getCustoPontos(),
                "PENDENTE"
            );
            transaction.set(resgateRef, novoResgate);

            return null;
        }).get(); // Garante a execução síncrona do bloco da transação

        return resgateRef.getId();
    }

    // Método público para o -- Agendamento -- chamar
    public void creditarPontos(String usuarioId, int quantidade, String origemId) throws ExecutionException, InterruptedException {
        DocumentReference userRef = firestore.collection("usuarios").document(usuarioId);
        
        firestore.runTransaction(transaction -> {
            DocumentSnapshot docUsuario = transaction.get(userRef).get();
            Usuario usuario = docUsuario.toObject(Usuario.class);
            if (usuario == null) throw new RuntimeException("Usuário não encontrado.");
            
            int novaPontuacao = usuario.getPontuacao() + quantidade;
            transaction.update(userRef, "pontuacao", novaPontuacao);
            
            return null;
        }).get();
    }
}