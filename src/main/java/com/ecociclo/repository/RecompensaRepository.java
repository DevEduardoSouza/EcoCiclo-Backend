package com.ecociclo.repository;

import org.springframework.stereotype.Repository;

import com.ecociclo.model.Recompensa;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class RecompensaRepository {

    private static final String COLLECTION = "recompensas";

    @Autowired
    private Firestore firestore;

    public String criar(Recompensa recompensa) throws ExecutionException, InterruptedException {
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

    // Retorna APENAS as recompensas com disponivel == true (Para a tela do Doador)
    public List<Recompensa> listarDisponiveis() throws ExecutionException, InterruptedException {
        // Query no Firestore filtrando pelo campo
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION)
                .whereEqualTo("disponivel", true)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Recompensa> recompensas = new ArrayList<>();

        for (QueryDocumentSnapshot doc : documents) {
            // Converte o documento do banco para a classe Java
            Recompensa recompensa = doc.toObject(Recompensa.class);
            
            // Injeta o ID do documento no objeto (@Exclude)
            recompensa.setId(doc.getId()); 
            
            recompensas.add(recompensa);
        }

        return recompensas;
    }

    // Retorna TODAS as recompensas, ignorando o status (Para o painel ADMIN)
    public List<Recompensa> listarTodas() throws ExecutionException, InterruptedException {
        // Busca todos os documentos da coleção sem aplicar filtros
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION).get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Recompensa> recompensas = new ArrayList<>();

        for (QueryDocumentSnapshot doc : documents) {
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
