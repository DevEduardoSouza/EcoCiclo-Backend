package com.ecociclo.model;

import com.google.cloud.firestore.annotation.Exclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

// TODO: Implementação será discutida com a equipe
@Data
@NoArgsConstructor
//@AllArgsConstructor
public class Chat {

    @Exclude
    private String id;

    private List<String> participantesId;
    private String ultimaMensagem;
    private long criadoEm;
    private long ultimaAtividade;
    private LocalDateTime dataCriacao;
    private String agendamentoId;

    public Chat(List<String> participantesId, String agendamentoId) {
        this.participantesId = participantesId;
        this.agendamentoId = agendamentoId;
        this.criadoEm = Instant.now().toEpochMilli();
        this.ultimaAtividade = Instant.now().toEpochMilli();
        this.dataCriacao = LocalDateTime.now();
    }
}
