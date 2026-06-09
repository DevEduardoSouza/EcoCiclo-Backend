package com.ecociclo.agendamento.model;

import com.google.cloud.firestore.annotation.Exclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agendamento {

    @Exclude
    private String id;

    private String doadorId;
    private String pontoColetaId;
    private String receptorId;
    private String dataHora;
    private String dataCriacao;
    private StatusAgendamento status;
    private List<String> materiais;
    private String observacoes;
    private int pontosGerados;
}