package com.ecociclo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PontoColeta {

    private String id;
    private String nome;
    private String endereco;
    private double latitude;
    private double longitude;
    private List<String> tiposResiduos; // ex: ["plástico", "vidro", "metal"]
    private String horarioFuncionamento;
}
