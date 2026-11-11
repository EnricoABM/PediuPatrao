package com.umc.pediupatrao.controller;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.umc.pediupatrao.dto.DashboardFiltro;
import com.umc.pediupatrao.dto.StatusPedidoDTO;
import com.umc.pediupatrao.dto.VendaDiariaDTO;
import com.umc.pediupatrao.service.DashboardService;
import com.umc.pediupatrao.service.ProdutoService;

@Controller
@RequestMapping("/dashboard")
@PreAuthorize("hasRole('GERENTE')")
public class DashboardController {
    private final DashboardService dashboardService;
    private final ProdutoService produtoService;

    public DashboardController(DashboardService dashboardService, ProdutoService produtoService) {
        this.dashboardService = dashboardService;
        this.produtoService = produtoService;
    }

    @GetMapping
    public String dashboard(
            @RequestParam(required = false) Integer periodo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) String produtoId,
            Model model) {

        model.addAttribute("content", "dashboard/index :: content");
        model.addAttribute("produtos", produtoService.listarProdutos());
        model.addAttribute("periodo", periodo);
        model.addAttribute("inicio", inicio);
        model.addAttribute("fim", fim);
        model.addAttribute("produtoId", produtoId == null ? "" : produtoId);
        model.addAttribute("periodos", List.of(7, 30, 90));

        try {
            DashboardFiltro filtro = dashboardService.criarFiltro(periodo, inicio, fim, produtoId);
            model.addAttribute("filtro", filtro);
            model.addAttribute("resumo", dashboardService.resumo(filtro));
            List<VendaDiariaDTO> vendas = dashboardService.vendas(filtro);
            List<StatusPedidoDTO> status = dashboardService.status(filtro);
            model.addAttribute("vendas", vendas);
            model.addAttribute("maiorVenda", vendas.stream().map(VendaDiariaDTO::faturamento)
                    .max(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
            model.addAttribute("statusPedidos", status);
            model.addAttribute("totalPedidosStatus", status.stream().mapToLong(StatusPedidoDTO::quantidade).sum());
            model.addAttribute("ranking", dashboardService.ranking(filtro, 5));
            
            model.addAttribute("tempos", dashboardService.tempos(filtro));
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
        }
        return "layout";
    }

    
}
