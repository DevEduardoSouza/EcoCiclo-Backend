package com.ecociclo.repository;

import com.ecociclo.model.PontoColeta;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class PontoColetaRepository {

    private static final String COLLECTION = "pontosColeta";

    @Autowired
    private Firestore firestore;

    public String salvar(PontoColeta pontoColeta) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = firestore.collection(COLLECTION).add(pontoColeta);
        return future.get().getId();
    }

    public PontoColeta buscarPorId(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = firestore.collection(COLLECTION).document(id).get().get();
        if (doc.exists()) {
            PontoColeta ponto = doc.toObject(PontoColeta.class);
            ponto.setId(doc.getId());
            return ponto;
        }
        return null;
    }

    public List<PontoColeta> listarTodos() throws ExecutionException, InterruptedException {
        List<PontoColeta> pontos = new ArrayList<>();
        List<QueryDocumentSnapshot> docs = firestore.collection(COLLECTION).get().get().getDocuments();
        for (QueryDocumentSnapshot doc : docs) {
            PontoColeta ponto = doc.toObject(PontoColeta.class);
            ponto.setId(doc.getId());
            pontos.add(ponto);
        }
        return pontos;
    }

    public void atualizar(String id, PontoColeta pontoColeta) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION).document(id).set(pontoColeta).get();
    }

    public void deletar(String id) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION).document(id).delete().get();
    }
}
