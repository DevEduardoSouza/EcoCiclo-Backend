package com.ecociclo.repository;

import com.ecociclo.model.Usuario;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

// Repositório responsável pelo acesso direto ao Firestore para a coleção "usuarios"
// Cada método realiza uma operação assíncrona no Firestore usando ApiFuture
@Repository
public class UsuarioRepository {

    // Nome da coleção no Firestore onde os documentos de usuários são armazenados
    private static final String COLLECTION = "usuarios";

    // Instância do Firestore injetada automaticamente pelo Spring (configurada em FirebaseConfig)
    @Autowired
    private Firestore firestore;

    // Salva um novo usuário no Firestore
    // .add() cria um documento com ID gerado automaticamente pelo Firestore
    // Retorna o ID do documento criado
    public String salvar(Usuario usuario) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = firestore.collection(COLLECTION).add(usuario);
        return future.get().getId();
    }

    // Busca um usuário pelo ID do documento no Firestore
    // .get().get() → o primeiro .get() dispara a requisição, o segundo aguarda o resultado (blocking)
    // Seta o ID manualmente pois o Firestore não inclui o ID do documento nos campos do objeto
    public Usuario buscarPorId(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = firestore.collection(COLLECTION).document(id).get().get();
        if (doc.exists()) {
            Usuario usuario = doc.toObject(Usuario.class);
            usuario.setId(doc.getId());
            return usuario;
        }
        return null;
    }

    // Lista todos os usuários da coleção
    // Itera sobre cada documento, converte para objeto Java e seta o ID manualmente
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

    // Atualiza um documento existente no Firestore
    // .set() substitui todos os campos do documento pelo novo objeto
    public void atualizar(String id, Usuario usuario) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION).document(id).set(usuario).get();
    }

    // Remove um documento do Firestore pelo ID
    public void deletar(String id) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION).document(id).delete().get();
    }
}
