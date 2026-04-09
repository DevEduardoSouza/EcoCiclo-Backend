package com.ecociclo.repository;

import com.ecociclo.model.Chat;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class ChatRepository {

    private static final String COLLECTION = "chats";

    @Autowired
    private Firestore firestore;

    public String salvar(Chat chat) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = firestore.collection(COLLECTION).add(chat);
        return future.get().getId();
    }

    public Chat buscarPorId(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = firestore.collection(COLLECTION).document(id).get().get();
        if (doc.exists()) {
            Chat chat = doc.toObject(Chat.class);
            chat.setId(doc.getId());
            return chat;
        }
        return null;
    }

    public List<Chat> listarTodos() throws ExecutionException, InterruptedException {
        List<Chat> chats = new ArrayList<>();
        List<QueryDocumentSnapshot> docs = firestore.collection(COLLECTION).get().get().getDocuments();
        for (QueryDocumentSnapshot doc : docs) {
            Chat chat = doc.toObject(Chat.class);
            chat.setId(doc.getId());
            chats.add(chat);
        }
        return chats;
    }

    public void atualizar(String id, Chat chat) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION).document(id).set(chat).get();
    }

    public void deletar(String id) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION).document(id).delete().get();
    }
}
