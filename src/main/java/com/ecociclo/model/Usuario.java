package com.ecociclo.model;

import com.google.cloud.firestore.annotation.Exclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Modelo que representa um usuário do sistema (doador ou cooperativa)
// @Data (Lombok) gera automaticamente getters, setters, toString, equals e hashCode
// @NoArgsConstructor e @AllArgsConstructor geram construtores sem e com todos os argumentos
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    // @Exclude impede que o campo "id" seja salvo como campo dentro do documento no Firestore
    // O ID é gerado automaticamente pelo Firestore como identificador do documento
    // Na leitura, o ID é recuperado via doc.getId() e setado manualmente no repository
    @Exclude
    private String id;
    private String nome;
    private String email;
    private String telefone;
    private String tipo; // "doador" ou "cooperativa"
    private int pontuacao; // pontos acumulados por reciclagem
}
