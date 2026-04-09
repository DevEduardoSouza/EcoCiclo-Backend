package com.ecociclo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recompensa {

    private String id;
    private String descricao;
    private int pontosNecessarios;
    private String tipo; // ex: "desconto", "brinde", "voucher"
    private boolean ativa;
}
