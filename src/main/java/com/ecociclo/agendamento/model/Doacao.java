package com.ecociclo.agendamento.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doacao {

    private String id;
    private String nome;
    private int quantidade;
    private String foto;
}