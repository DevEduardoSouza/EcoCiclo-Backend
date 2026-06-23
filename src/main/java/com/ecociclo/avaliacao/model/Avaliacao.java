package com.ecociclo.avaliacao.model;

import com.google.cloud.firestore.annotation.Exclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Avaliacao {

    @Exclude
    private String id;

    private String tipo;
    private String coletor;
    private String data;
    private String comentario;
}
