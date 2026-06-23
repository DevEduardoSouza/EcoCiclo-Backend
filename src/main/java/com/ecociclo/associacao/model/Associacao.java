package com.ecociclo.associacao.model;

import com.google.cloud.firestore.annotation.Exclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Associacao {

    @Exclude
    private String id;

    private String nomeAssociacao;
    private StatusAssociacao status = StatusAssociacao.PENDENTE;
}
