package com.ecociclo.avaliacao.repository;

import com.ecociclo.avaliacao.model.Avaliacao;
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
public class AvaliacaoRepository {

    private static final String COLLECTION = "avaliacoes";

    private final Firestore firestore;

    public AvaliacaoRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public String salvar(Avaliacao avaliacao) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = firestore.collection(COLLECTION).add(avaliacao);
        return future.get().getId();
    }

    public Avaliacao buscarPorId(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = firestore.collection(COLLECTION).document(id).get().get();

        if (!doc.exists()) {
            return null;
        }

        Avaliacao avaliacao = doc.toObject(Avaliacao.class);
        avaliacao.setId(doc.getId());
        return avaliacao;
    }

    public List<Avaliacao> listarTodos() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION).get();
        List<Avaliacao> avaliacoes = new ArrayList<>();

        for (QueryDocumentSnapshot doc : future.get().getDocuments()) {
            Avaliacao avaliacao = doc.toObject(Avaliacao.class);
            avaliacao.setId(doc.getId());
            avaliacoes.add(avaliacao);
        }

        return avaliacoes;
    }

    public void atualizar(String id, Avaliacao avaliacao) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION).document(id).set(avaliacao).get();
    }

    public void deletar(String id) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION).document(id).delete().get();
    }
}
