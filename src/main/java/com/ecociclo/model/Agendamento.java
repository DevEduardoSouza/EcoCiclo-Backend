package com.ecociclo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agendamento {

    private String id;
    private String usuarioId;
    private String pontoColetaId;
    private String dataHora;
    private String tipoResiduo;
    private String status; // "pendente", "confirmado", "concluido", "cancelado"
}
