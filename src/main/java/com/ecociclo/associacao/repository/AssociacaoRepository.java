package com.ecociclo.associacao.repository;

import com.ecociclo.associacao.model.Associacao;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class AssociacaoRepository {

    private static final String COLLECTION = "associacoes";

    private final Firestore firestore;

    public AssociacaoRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public String salvar(Associacao associacao) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = firestore.collection(COLLECTION).add(associacao);
        return future.get().getId();
    }

    public Associacao buscarPorId(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = firestore.collection(COLLECTION).document(id).get().get();

        if (!doc.exists()) {
            return null;
        }

        Associacao associacao = doc.toObject(Associacao.class);
        associacao.setId(doc.getId());
        return associacao;
    }

    public List<Associacao> listarTodos() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION).get();
        List<Associacao> associacoes = new ArrayList<>();

        for (QueryDocumentSnapshot doc : future.get().getDocuments()) {
            Associacao associacao = doc.toObject(Associacao.class);
            associacao.setId(doc.getId());
            associacoes.add(associacao);
        }

        return associacoes;
    }

    public void atualizar(String id, Associacao associacao) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION).document(id).set(associacao).get();
    }

    public void deletar(String id) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION).document(id).delete().get();
    }
}
