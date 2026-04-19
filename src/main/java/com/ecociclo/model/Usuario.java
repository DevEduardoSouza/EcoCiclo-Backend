package com.ecociclo.model;

import com.google.cloud.firestore.annotation.Exclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Exclude
    private String id;
    private String nome;
    private String email;
    private String telefone;
    private TipoUsuario tipo;
    // ID da associação dona do receptor. Null para ADMIN, ASSOCIACAO e DOADOR.
    private String associacaoId;
    private int pontuacao;
}
