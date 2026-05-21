package com.ecociclo.repository;

import com.ecociclo.model.Chat;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

// TODO: Implementação será discutida com a equipe
@Repository
public class ChatRepository {

    @Autowired
    private Firestore firestore;

    public String criarChat(Chat chat) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = firestore.collection("chats").add(chat);
        return future.get().getId();
    }

    public Optional<Chat> buscarPorId(String chatId) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = firestore.collection("chats")
                .document(chatId)
                .get()
                .get();

        if (!doc.exists()) return Optional.empty();

        Chat chat = doc.toObject(Chat.class);
        chat.setId(doc.getId());
        return Optional.of(chat);
    }

    public List<Chat> buscarPorUsuario(String usuarioId)
            throws ExecutionException, InterruptedException {

        QuerySnapshot snapshot = firestore.collection("chats")
                .whereArrayContains("participantesId", usuarioId)
                .orderBy("ultimaAtividade", Query.Direction.DESCENDING) // mais recente primeiro
                .get()
                .get();

        List<Chat> chats = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            Chat chat = doc.toObject(Chat.class);
            chat.setId(doc.getId());
            chats.add(chat);
        }
        return chats;
    }

    public Optional<Chat> buscarPorParticipantes(List<String> participantesId)
            throws ExecutionException, InterruptedException {

        // O Firestore não suporta dois whereArrayContains na mesma query,
        // então buscamos pelo primeiro participante e filtramos o segundo em memória
        QuerySnapshot snapshot = firestore.collection("chats")
                .whereArrayContains("participantesId", participantesId.get(0))
                .get()
                .get();

        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            Chat chat = doc.toObject(Chat.class);
            chat.setId(doc.getId());

            // Verifica se os participantes são exatamente os mesmos (independente de ordem)
            List<String> existentes = chat.getParticipantesId();
            if (existentes != null
                    && existentes.size() == participantesId.size()
                    && existentes.containsAll(participantesId)) {
                return Optional.of(chat);
            }
        }

        return Optional.empty();
    }

    public void atualizarUltimaAtividade(String chatId, String textoUltimaMensagem)
            throws ExecutionException, InterruptedException {

        firestore.collection("chats")
                .document(chatId)
                .update(
                        "ultimaMensagem", textoUltimaMensagem,
                        "ultimaAtividade", Instant.now().toEpochMilli()
                )
                .get(); // update() altera só esses campos, sem sobrescrever o restante
    }

}
