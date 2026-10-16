package com.umc.pediupatrao.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Document(collection = "auditoria_logs")
public class AuditoriaLog {

    @Id
    private String id;
    
    private String usuarioUsername;
    private String perfilUsuario;
    private LocalDateTime dataHora;
    private String tipoOperacao; 
    private String entidadeAfetada; 
    private String entidadeId;
    
    private Map<String, Object[]> camposModificados;
    
    private String justificativa;
}
