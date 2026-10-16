package com.umc.pediupatrao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umc.pediupatrao.entity.AuditoriaLog;
import com.umc.pediupatrao.repository.AuditoriaRepository;

@Controller 
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final AuditoriaRepository auditoriaRepository;

    AuditoriaController(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('AUDIT_READ_ALL')")
    public String listarLogs(Model model) {
        List<AuditoriaLog> logs = auditoriaRepository.findAll();
        model.addAttribute("logs", logs);
        model.addAttribute("content", "auditoria/lista :: content");
        return "layout";
    }

}
