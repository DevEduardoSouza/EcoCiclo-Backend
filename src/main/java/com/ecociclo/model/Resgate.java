package com.ecociclo.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.google.cloud.firestore.annotation.Exclude;
import java.time.LocalDateTime;

@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class Resgate {
    @Exclude private String id;
    private String recompensaId;
    private String usuarioId;
    private LocalDateTime dataResgate;
    private int pontosGastos;       // Guardamos o valor exato do momento do resgate
    private String status;          // "PENDENTE", "ENTREGUE", "CANCELADO"
}
