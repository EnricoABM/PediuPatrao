package com.umc.pediupatrao.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.umc.pediupatrao.dto.ProdutoInfoDTO;
import com.umc.pediupatrao.dto.ProdutoRegisterDTO;
import com.umc.pediupatrao.entity.Produto;
import com.umc.pediupatrao.service.ProdutoService;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    private final Logger log = LoggerFactory.getLogger(ProdutoController.class);

    ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public String produtos(Model model) {
        List<ProdutoInfoDTO> produtos = produtoService.listarProdutos().stream().map(ProdutoInfoDTO::from).toList();
        model.addAttribute("produtos", produtos);
        model.addAttribute("content", "produtos/lista :: content");
        log.info("Carregando fragmento: produtos/lista :: content");
        return "layout";
    }

    @GetMapping("novo")
    public String novoProdutoForm(Model model) {
        model.addAttribute("produto", new Produto());
        model.addAttribute("content", "produtos/form :: content");
        return "layout";
    }

    @PostMapping
    public String novoProduto(@ModelAttribute ProdutoRegisterDTO dto, RedirectAttributes redirectAttributes) {
        produtoService.salvar(dto.toModel());
        redirectAttributes.addFlashAttribute("sucesso", "Produto cadastrado com sucesso!");
        return "redirect:/produtos";
    }

    @PostMapping("/deletar/{id}")
    public String excluirProduto(@PathVariable String id, RedirectAttributes redirectAttributes) {
        produtoService.excluir(id);
        redirectAttributes.addFlashAttribute("sucesso", "Usuário removido com sucesso!");
        return "redirect:/produtos";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable("id") String id, RedirectAttributes redirectAttributes, Model model) {
        boolean encontrado = produtoService.buscarPorId(id).map(p -> {
            model.addAttribute("produto", p);
            return true;
        }).orElse(false);

        if (!encontrado) {
            redirectAttributes.addFlashAttribute("erro", "Produto não encontrado.");
            return "redirect:/produtos";
        }

        model.addAttribute("content", "produtos/form :: content");
        return "layout";
    }

}
