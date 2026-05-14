package com.ecociclo.service;

import com.ecociclo.model.Mensagem;
import com.ecociclo.repository.ChatRepository;
import com.ecociclo.repository.MensagemRepository;
import com.ecociclo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class MensagemService {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private ChatRepository chatRepository;          // valida se o chat existe

    @Autowired
    private UsuarioRepository usuarioRepository;    // valida se o remetente existe e é participante

    public String enviar(String chatId, Mensagem mensagem)
            throws ExecutionException, InterruptedException {

        // 1. Chat deve existir
        var chat = chatRepository.buscarPorId(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat '" + chatId + "' não encontrado."));

        // 2. Remetente obrigatório
        if (mensagem.getRemetenteId() == null || mensagem.getRemetenteId().isBlank()) {
            throw new IllegalArgumentException("Campo 'remetenteId' é obrigatório.");
        }

        // 3. Remetente deve ser participante do chat
        if (!chat.getParticipantesId().contains(mensagem.getRemetenteId())) {
            throw new IllegalArgumentException(
                    "Remetente '" + mensagem.getRemetenteId() + "' não é participante deste chat.");
        }

        // 4. Conteúdo não pode ser vazio
        if (mensagem.getConteudo() == null || mensagem.getConteudo().isBlank()) {
            throw new IllegalArgumentException("Mensagem não pode ser vazia.");
        }

        mensagem.setChatId(chatId);
        return mensagemRepository.enviar(chatId, mensagem);
    }

    public List<Mensagem> listar(String chatId, Integer limite, String cursorMensagemId)
            throws ExecutionException, InterruptedException {

        // Chat deve existir antes de listar
        chatRepository.buscarPorId(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat '" + chatId + "' não encontrado."));

        return mensagemRepository.listar(chatId, limite, cursorMensagemId);
    }

    public void deletar(String chatId, String mensagemId, String solicitanteId)
            throws ExecutionException, InterruptedException {

        // Só o próprio remetente pode deletar — você pode relaxar isso depois
        // buscando a mensagem e comparando o remetenteId
        mensagemRepository.deletar(chatId, mensagemId);
    }
}