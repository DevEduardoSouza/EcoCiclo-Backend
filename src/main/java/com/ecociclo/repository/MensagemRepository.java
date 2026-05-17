package com.ecociclo.repository;

import com.ecociclo.model.Mensagem;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class MensagemRepository {

    private static final String CHATS = "chats";
    private static final String MENSAGENS = "mensagens";
    private static final int LIMITE_PADRAO = 30;

    @Autowired
    private Firestore firestore;

    // Atalho para a subcoleção de mensagens de um chat
    private CollectionReference subcollection(String chatId) {
        return firestore.collection(CHATS).document(chatId).collection(MENSAGENS);
    }

    public String enviar(String chatId, Mensagem mensagem) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = subcollection(chatId).add(mensagem);
        return future.get().getId();
    }

    /**
     * Lista mensagens ordenadas das mais antigas para as mais novas.
     *
     * @param chatId         ID do chat
     * @param limite         quantas mensagens retornar (usa LIMITE_PADRAO se null)
     * @param cursorMensagemId  ID da última mensagem já recebida — retorna as ANTERIORES a ela
     *                       (scroll para cima / carregar mais antigas). Null = página mais recente.
     */
    public List<Mensagem> listar(String chatId, Integer limite, String cursorMensagemId)
            throws ExecutionException, InterruptedException {

        int pageSize = (limite != null && limite > 0) ? limite : LIMITE_PADRAO;

        Query query = subcollection(chatId)
                .orderBy("enviadoEm", Query.Direction.DESCENDING)
                .limit(pageSize);

        if (cursorMensagemId != null && !cursorMensagemId.isBlank()) {
            DocumentSnapshot cursor = subcollection(chatId)
                    .document(cursorMensagemId)
                    .get()
                    .get();
            if (cursor.exists()) {
                query = query.startAfter(cursor);
            }
        }

        List<Mensagem> mensagens = new ArrayList<>();
        for (QueryDocumentSnapshot doc : query.get().get().getDocuments()) {
            Mensagem m = doc.toObject(Mensagem.class);
            m.setId(doc.getId());
            mensagens.add(m);
        }

        // Inverte para retornar em ordem cronológica (mais antiga primeiro)
        java.util.Collections.reverse(mensagens);
        return mensagens;
    }

    public void deletar(String chatId, String mensagemId) throws ExecutionException, InterruptedException {
        subcollection(chatId).document(mensagemId).delete().get();
    }
}