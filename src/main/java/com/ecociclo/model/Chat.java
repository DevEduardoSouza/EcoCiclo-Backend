package com.ecociclo.model;

import com.google.cloud.firestore.annotation.Exclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
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
    private long atualizadoEm;

    public Chat(List<String> participantesId) {
        this.participantesId = participantesId;
        this.criadoEm = Instant.now().toEpochMilli();
        this.atualizadoEm = Instant.now().toEpochMilli();
    }
}
