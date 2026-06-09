package com.ecociclo.resgate.repository;


import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.ecociclo.resgate.model.Resgate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class ResgateRepository {

    private final Firestore firestore;
    private static final String COLLECTION = "resgates";

    public ResgateRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    // Salva um novo resgate no banco
    public String salvar(Resgate resgate) throws ExecutionException, InterruptedException {
        DocumentReference docRef;
        
        // Verifica se o resgate já veio com ID
        // Se não tiver ID, deixa o Firestore gerar um automático chamando .document() vazio.
        if (resgate.getId() != null && !resgate.getId().isEmpty()) {
            docRef = firestore.collection(COLLECTION).document(resgate.getId());
        } else {
            docRef = firestore.collection(COLLECTION).document();
        }
        
        // O .set() envia os dados. Como 'id' tem @Exclude no Model, ele não vai pro corpo do documento.
        docRef.set(resgate).get(); 
        
        return docRef.getId(); // Retorna o ID salvo
    }

    // Lista o histórico de resgates de um doador específico
    public List<Resgate> listarPorUsuario(String usuarioId) throws ExecutionException, InterruptedException {
        // Query no Firestore: WHERE usuarioId == "o_id_passado_no_parametro"
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION)
                .whereEqualTo("usuarioId", usuarioId)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Resgate> resgates = new ArrayList<>();

        for (QueryDocumentSnapshot doc : documents) {
            // Converte o documento do banco para a classe Java
            Resgate resgate = doc.toObject(Resgate.class);
            
            // Injeta o ID do documento de volta no objeto para o Front-end saber qual é
            resgate.setId(doc.getId()); 
            
            resgates.add(resgate);
        }

        return resgates;
    }
}