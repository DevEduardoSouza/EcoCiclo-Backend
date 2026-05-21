package com.ecociclo.service;

import com.ecociclo.model.Chat;
import com.ecociclo.repository.ChatRepository;
import com.ecociclo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

// TODO: Implementação será discutida com a equipe
@Service
public class ChatService {

    @Autowired
    private ChatRepository repositorio;


    public String novoChat(Chat chat) throws ExecutionException, InterruptedException {
        if (chat.getParticipantesId() == null || chat.getParticipantesId().size() < 2) {
            throw new IllegalArgumentException("Um chat precisa de pelo menos 2 participantes.");
        }

        // Verifica se já existe chat com os mesmos participantes
        Optional<Chat> existente = repositorio.buscarPorParticipantes(chat.getParticipantesId());
        if (existente.isPresent()) {
            // Retorna o ID do chat existente em vez de lançar erro
            return existente.get().getId();
        }

        return repositorio.criarChat(chat);
    }

    public Optional<Chat> buscarChat(String chatId) throws ExecutionException, InterruptedException {
        return repositorio.buscarPorId(chatId);
    }

    public List<Chat> listarChatsDoUsuario(String usuarioId) throws ExecutionException, InterruptedException {
        if (usuarioId == null || usuarioId.isBlank()) {
            throw new IllegalArgumentException("ID do usuário é obrigatório.");
        }
        return repositorio.buscarPorUsuario(usuarioId);
    }

    public Optional<Chat> buscarPorParticipantes(List<String> participantesId)
            throws ExecutionException, InterruptedException {
        return repositorio.buscarPorParticipantes(participantesId);
    }

}
