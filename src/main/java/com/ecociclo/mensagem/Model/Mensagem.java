package com.ecociclo.mensagem.Model;
import com.google.cloud.firestore.annotation.Exclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class Mensagem {

    @Exclude
    private String id;

    private String chatId;
    private String autorId;   //ID do usuário que enviou
    private String texto;
    private TipoMensagem tipo;    //TEXTO (expansível para IMAGEM, ARQUIVO, SISTEMA), só esta aqui pois fiz esse codigo com base em usuario
    private long enviadoEm;       //epoch millis — usado para ordenação e cursor
    private boolean lida;       // Campo para marcar se a mensagem foi lida pelo destinatário. Útil para notificações e UX.

    public enum TipoMensagem {
        TEXTO
    }

    public Mensagem(String chatId, String autorId, String texto) {
        this.chatId = chatId;
        this.autorId = autorId;
        this.texto = texto;
        this.tipo = TipoMensagem.TEXTO;
        this.enviadoEm = Instant.now().toEpochMilli();
        this.lida = false;
    }
}