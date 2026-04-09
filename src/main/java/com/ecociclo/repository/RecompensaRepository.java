package com.ecociclo.repository;

import com.ecociclo.model.Recompensa;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class RecompensaRepository {

    private static final String COLLECTION = "recompensas";

    @Autowired
    private Firestore firestore;

    public String salvar(Recompensa recompensa) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = firestore.collection(COLLECTION).add(recompensa);
        return future.get().getId();
    }

    public Recompensa buscarPorId(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = firestore.collection(COLLECTION).document(id).get().get();
        if (doc.exists()) {
            Recompensa recompensa = doc.toObject(Recompensa.class);
            recompensa.setId(doc.getId());
            return recompensa;
        }
        return null;
    }

    public List<Recompensa> listarTodos() throws ExecutionException, InterruptedException {
        List<Recompensa> recompensas = new ArrayList<>();
        List<QueryDocumentSnapshot> docs = firestore.collection(COLLECTION).get().get().getDocuments();
        for (QueryDocumentSnapshot doc : docs) {
            Recompensa recompensa = doc.toObject(Recompensa.class);
            recompensa.setId(doc.getId());
            recompensas.add(recompensa);
        }
        return recompensas;
    }

    public void atualizar(String id, Recompensa recompensa) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION).document(id).set(recompensa).get();
    }

    public void deletar(String id) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION).document(id).delete().get();
    }
}
