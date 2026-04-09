package com.ecociclo.service;

import com.ecociclo.model.Chat;
import com.ecociclo.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    public String criar(Chat chat) throws ExecutionException, InterruptedException {
        return chatRepository.salvar(chat);
    }

    public Chat buscarPorId(String id) throws ExecutionException, InterruptedException {
        return chatRepository.buscarPorId(id);
    }

    public List<Chat> listarTodos() throws ExecutionException, InterruptedException {
        return chatRepository.listarTodos();
    }

    public void atualizar(String id, Chat chat) throws ExecutionException, InterruptedException {
        chatRepository.atualizar(id, chat);
    }

    public void deletar(String id) throws ExecutionException, InterruptedException {
        chatRepository.deletar(id);
    }
}
