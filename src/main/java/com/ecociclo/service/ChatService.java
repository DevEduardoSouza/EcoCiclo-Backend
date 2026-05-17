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

    @Autowired
    private UsuarioRepository usuarioRepository; //dependência para o metodo novoChat

    public String novoChat(Chat chat) throws ExecutionException, InterruptedException {
        if (chat.getParticipantesId() == null || chat.getParticipantesId().size() < 2) {
            throw new IllegalArgumentException("Um chat precisa de pelo menos 2 participantes.");
        }

        // validar se todos os participantes existem
        for (String uid : chat.getParticipantesId()) {
            if (usuarioRepository.buscarPorId(uid) == null) {
                throw new IllegalArgumentException("Participante não encontrado: " + uid);
            }
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
}
