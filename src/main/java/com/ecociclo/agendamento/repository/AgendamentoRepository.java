package com.ecociclo.agendamento.repository;

import com.ecociclo.agendamento.model.Agendamento;
import com.ecociclo.agendamento.model.StatusAgendamento;
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

    public String salvar(Agendamento agendamento) throws ExecutionException, InterruptedException{
        ApiFuture<DocumentReference> future = firestore.collection(COLLECTION).add(agendamento);
        return future.get().getId();
    }

    public Agendamento buscarPorId(String id) throws ExecutionException, InterruptedException{
        DocumentSnapshot doc =  firestore.collection(COLLECTION).document(id).get().get();
        if (doc.exists()){
            Agendamento ag = doc.toObject(Agendamento.class);
            ag.setId(doc.getId());
            return ag;
        }
        return null;
    }

    public List<Agendamento> listarTodos() throws ExecutionException, InterruptedException{
        return listarPorFiltros(null, null, null, null, null);
    }

    // Ambos os parâmetros null equivalem a listar tudo — mesmo padrão do UsuarioRepository
    public List<Agendamento> listarPorFiltros(String doadorId, String receptorId,
                                              StatusAgendamento status, String dataInicio, String dataFim)
            throws ExecutionException, InterruptedException {

        Query query = firestore.collection(COLLECTION);

        if (doadorId != null && !doadorId.isBlank())
            query = query.whereEqualTo("doadorId", doadorId);

        if (receptorId != null && !receptorId.isBlank())
            query = query.whereEqualTo("receptorId", receptorId);

        if (status != null)
            query = query.whereEqualTo("status", status.name());

        // ISO-8601 é ordenável como String, então comparação de data funciona direto
        if (dataInicio != null && !dataInicio.isBlank())
            query = query.whereGreaterThanOrEqualTo("dataHora", dataInicio);

        if (dataFim != null && !dataFim.isBlank())
            query = query.whereLessThanOrEqualTo("dataHora", dataFim);

        List<Agendamento> lista = new ArrayList<>();
        for (QueryDocumentSnapshot doc : query.get().get().getDocuments()) {
            Agendamento ag = doc.toObject(Agendamento.class);
            ag.setId(doc.getId());
            lista.add(ag);
        }
        return lista;
    }

    public void atualizar(String id, Agendamento agendamento) throws ExecutionException, InterruptedException{
        firestore.collection(COLLECTION).document(id).set(agendamento).get();
    }


}