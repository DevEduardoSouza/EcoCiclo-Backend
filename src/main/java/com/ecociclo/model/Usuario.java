package com.ecociclo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private String id;
    private String nome;
    private String email;
    private String telefone;
    private String tipo; // "doador" ou "cooperativa"
    private int pontuacao;
}
