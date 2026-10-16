package com.umc.pediupatrao.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.umc.pediupatrao.entity.AuditoriaLog;
import com.umc.pediupatrao.repository.AuditoriaRepository;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    public void registrar(String tipoOperacao, String entidadeAfetada, String entidadeId,
            Map<String, Object[]> camposModificados, String justificativa) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.isAuthenticated()) ? auth.getName() : "Sistema";

        String perfil = (auth != null) ? auth.getAuthorities().toString() : "ANONYMOUS";

        AuditoriaLog log = new AuditoriaLog();
        log.setUsuarioUsername(username);
        log.setPerfilUsuario(perfil);
        log.setDataHora(LocalDateTime.now());
        log.setTipoOperacao(tipoOperacao);
        log.setEntidadeAfetada(entidadeAfetada);
        log.setEntidadeId(entidadeId);
        log.setCamposModificados(camposModificados);
        log.setJustificativa(justificativa);

        auditoriaRepository.save(log);
    }
}
