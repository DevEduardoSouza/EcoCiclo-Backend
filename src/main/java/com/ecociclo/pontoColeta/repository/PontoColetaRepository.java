package com.ecociclo.pontoColeta.repository;

import com.ecociclo.pontoColeta.model.Endereco;
import com.ecociclo.pontoColeta.model.PontoColeta;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class PontoColetaRepository {

    private static final String COLLECTION = "pontos_coleta";

    private final Firestore firestore;

    public PontoColetaRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public String salvar(PontoColeta pontoColeta) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION).document();
        pontoColeta.setId(docRef.getId());
        docRef.set(pontoColeta).get();
        return docRef.getId();
    }

    public PontoColeta buscarPorId(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = firestore.collection(COLLECTION).document(id).get().get();

        if (doc.exists()) {
            return converterDocumento(doc);
        }

        return null;
    }

    public List<PontoColeta> listarTodos() throws ExecutionException, InterruptedException {
        List<PontoColeta> pontos = new ArrayList<>();

        List<QueryDocumentSnapshot> docs = firestore.collection(COLLECTION)
                .get().get().getDocuments();

        for (QueryDocumentSnapshot doc : docs) {
            PontoColeta ponto = converterDocumento(doc);
            if (ponto != null) {
                pontos.add(ponto);
            }
        }

        return pontos;
    }

    public void atualizar(String id, PontoColeta pontoColeta) throws ExecutionException, InterruptedException {
        pontoColeta.setId(id);
        firestore.collection(COLLECTION).document(id).set(pontoColeta).get();
    }

    public void deletar(String id) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION).document(id).delete().get();
    }

    private PontoColeta converterDocumento(DocumentSnapshot doc) {
        Map<String, Object> dados = doc.getData();
        if (dados == null) {
            return null;
        }

        return new PontoColeta(
                doc.getId(),
                comoTexto(dados.get("nome")),
                comoTexto(dados.get("responsavel")),
                comoTexto(dados.get("telefone")),
                comoTexto(dados.get("email")),
                converterEndereco(dados.get("endereco")),
                comoBooleano(dados.get("ativo"))
        );
    }

    @SuppressWarnings("unchecked")
    private Endereco converterEndereco(Object valor) {
        if (!(valor instanceof Map<?, ?> endereco)) {
            return null;
        }

        Map<String, Object> dadosEndereco = (Map<String, Object>) endereco;
        return new Endereco(
                comoTexto(dadosEndereco.get("logradouro")),
                comoTexto(dadosEndereco.get("bairro")),
                comoTexto(dadosEndereco.get("cidade")),
                comoTexto(dadosEndereco.get("estado")),
                comoTexto(dadosEndereco.get("cep"))
        );
    }

    private String comoTexto(Object valor) {
        return valor == null ? null : valor.toString();
    }

    private Boolean comoBooleano(Object valor) {
        if (valor instanceof Boolean booleano) {
            return booleano;
        }

        if (valor instanceof String texto && !texto.isBlank()) {
            return Boolean.parseBoolean(texto);
        }

        return true;
    }
}
