package com.ecociclo.pontoColeta.model;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter     
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class PontoColeta {

    private String id;
    private String nome;
    private String responsavel;
    private String telefone;
    private String email;
    private String endereco;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private Boolean ativo;

}
