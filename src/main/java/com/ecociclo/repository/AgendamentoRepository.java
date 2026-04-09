package com.ecociclo.repository;

import com.ecociclo.model.Agendamento;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class AgendamentoRepository {

    private static final String COLLECTION = "agendamentos";

    @Autowired
    private Firestore firestore;

    public String salvar(Agendamento agendamento) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = firestore.collection(COLLECTION).add(agendamento);
        return future.get().getId();
    }

    public Agendamento buscarPorId(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = firestore.collection(COLLECTION).document(id).get().get();
        if (doc.exists()) {
            Agendamento agendamento = doc.toObject(Agendamento.class);
            agendamento.setId(doc.getId());
            return agendamento;
        }
        return null;
    }

    public List<Agendamento> listarTodos() throws ExecutionException, InterruptedException {
        List<Agendamento> agendamentos = new ArrayList<>();
        List<QueryDocumentSnapshot> docs = firestore.collection(COLLECTION).get().get().getDocuments();
        for (QueryDocumentSnapshot doc : docs) {
            Agendamento agendamento = doc.toObject(Agendamento.class);
            agendamento.setId(doc.getId());
            agendamentos.add(agendamento);
        }
        return agendamentos;
    }

    public void atualizar(String id, Agendamento agendamento) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION).document(id).set(agendamento).get();
    }

    public void deletar(String id) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION).document(id).delete().get();
    }
}
