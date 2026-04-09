package com.ecociclo.repository;

import com.ecociclo.model.Usuario;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class UsuarioRepository {

    private static final String COLLECTION = "usuarios";

    @Autowired
    private Firestore firestore;

    public String salvar(Usuario usuario) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = firestore.collection(COLLECTION).add(usuario);
        return future.get().getId();
    }

    public Usuario buscarPorId(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = firestore.collection(COLLECTION).document(id).get().get();
        if (doc.exists()) {
            Usuario usuario = doc.toObject(Usuario.class);
            usuario.setId(doc.getId());
            return usuario;
        }
        return null;
    }

    public List<Usuario> listarTodos() throws ExecutionException, InterruptedException {
        List<Usuario> usuarios = new ArrayList<>();
        List<QueryDocumentSnapshot> docs = firestore.collection(COLLECTION).get().get().getDocuments();
        for (QueryDocumentSnapshot doc : docs) {
            Usuario usuario = doc.toObject(Usuario.class);
            usuario.setId(doc.getId());
            usuarios.add(usuario);
        }
        return usuarios;
    }

    public void atualizar(String id, Usuario usuario) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION).document(id).set(usuario).get();
    }

    public void deletar(String id) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION).document(id).delete().get();
    }
}
