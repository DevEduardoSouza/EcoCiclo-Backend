package com.ecociclo.recompensa.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.google.cloud.firestore.annotation.Exclude;

// TODO: Implementação será discutida com a equipe
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recompensa {
    @Exclude private String id;
    private String nome;
    private String descricao;
    private int custoPontos;          // pontos necessários
    private String parceiro;          // nome da empresa parceira
    private boolean disponivel;
    private Integer estoque;          // null = ilimitado
    private String imagemUrl;
}
