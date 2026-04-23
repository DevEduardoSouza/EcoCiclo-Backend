package com.ecociclo.repository;

import com.ecociclo.model.TipoUsuario;
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

    public Usuario buscarPorEmail(String email) throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> docs = firestore.collection(COLLECTION)
                .whereEqualTo("email", email)
                .limit(1)
                .get().get().getDocuments();
        if (docs.isEmpty()) return null;
        Usuario usuario = docs.get(0).toObject(Usuario.class);
        usuario.setId(docs.get(0).getId());
        return usuario;
    }

    public Usuario buscarPorFirebaseUid(String firebaseUid) throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> docs = firestore.collection(COLLECTION)
                .whereEqualTo("firebaseUid", firebaseUid)
                .limit(1)
                .get().get().getDocuments();
        if (docs.isEmpty()) return null;
        Usuario usuario = docs.get(0).toObject(Usuario.class);
        usuario.setId(docs.get(0).getId());
        return usuario;
    }

    public List<Usuario> listarTodos() throws ExecutionException, InterruptedException {
        return listarPorFiltros(null, null);
    }

    // Lista usuários aplicando filtros opcionais por tipo e/ou associacaoId.
    // Ambos os parâmetros null equivalem a listar tudo.
    public List<Usuario> listarPorFiltros(TipoUsuario tipo, String associacaoId)
            throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION);
        if (tipo != null) {
            query = query.whereEqualTo("tipo", tipo.name());
        }
        if (associacaoId != null && !associacaoId.isBlank()) {
            query = query.whereEqualTo("associacaoId", associacaoId);
        }

        List<Usuario> usuarios = new ArrayList<>();
        for (QueryDocumentSnapshot doc : query.get().get().getDocuments()) {
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
