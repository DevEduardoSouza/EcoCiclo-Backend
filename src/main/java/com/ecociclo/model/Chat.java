package com.ecociclo.model;

import com.google.cloud.firestore.annotation.Exclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    @Exclude
    private String id;
    private String remetenteId;
    private String destinatarioId;
    private String mensagem;
    private String dataEnvio;
    private boolean lida;
}
