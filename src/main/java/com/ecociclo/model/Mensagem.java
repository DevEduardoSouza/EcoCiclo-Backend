package com.ecociclo.model;
import com.google.cloud.firestore.annotation.Exclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class Mensagem {

    @Exclude
    private String id;

    private String chatId;        //redundante mas útil para queries diretas
    private String remetenteId;   //ID do usuário que enviou
    private String conteudo;      //texto da mensagem
    private TipoMensagem tipo;    //TEXTO (expansível para IMAGEM, ARQUIVO, SISTEMA)
    private long enviadoEm;       //epoch millis — usado para ordenação e cursor

    public enum TipoMensagem {
        TEXTO
    }

    public Mensagem(String chatId, String remetenteId, String conteudo) {
        this.chatId = chatId;
        this.remetenteId = remetenteId;
        this.conteudo = conteudo;
        this.tipo = TipoMensagem.TEXTO;
        this.enviadoEm = Instant.now().toEpochMilli();
    }
}