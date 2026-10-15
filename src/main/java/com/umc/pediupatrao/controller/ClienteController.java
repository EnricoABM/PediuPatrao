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

import com.umc.pediupatrao.dto.ClienteInfoDTO;
import com.umc.pediupatrao.dto.ClienteRegisterDTO;
import com.umc.pediupatrao.entity.Cliente;
import com.umc.pediupatrao.service.ClienteService;

/**
 * Controler para o módulo de clientes da aplicação
 * 
 * @author Enrico Bertozzi
 * @author Alessandro Aparecido da Silva
 * @version 2.0
 */
@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    private final static Logger log = LoggerFactory.getLogger(ClienteController.class);

    ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * Endpoint para listagem de clientes
     * 
     * @param model Objeto com dados da página
     * 
     * @return Carrega o layout com fragmento de clientes
     */
    @GetMapping
    public String clientes(Model model) {
        List<ClienteInfoDTO> clientes = clienteService
                .listarClientes()
                .stream()
                .map(ClienteInfoDTO::fromModel)
                .toList();
        model.addAttribute("clientes", clientes);
        model.addAttribute("content", "clientes/lista :: content");
        log.info("Carregando fragmento: clientes/lista :: content");
        return "layout";
    }

    /**
     * Endpoint para o formulário de criação de novo usuário
     * 
     * @param model Objeto com dados da página
     * 
     * @return Formulário de criação de usuários
     */
    @GetMapping("/novo")
    public String novoClienteForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("content", "clientes/form :: content");
        return "layout";
    }

    /**
     * Endpoint para cadastro de usuários
     * 
     * @param cliente            Objeto com dados do cliente
     * 
     * @param redirectAttributes Objeto para redirecionamento da página.
     *
     * @return Redireciona o usuário para tela de clientes
     */
    @PostMapping("/salvar")
    public String salvarCliente(@ModelAttribute ClienteRegisterDTO dto,
            RedirectAttributes redirectAttributes) {
        clienteService.salvar(dto.toModel());
        redirectAttributes.addFlashAttribute("sucesso", "Cliente criado com sucesso!");
        return "redirect:/clientes";
    }

    /**
     * Endpoint para o formulário de atualização de um cliente
     * 
     * @param id                 Identificador do cliente
     * @param model              Objeto com dados da página
     * @param redirectAttributes Objeto para redirecionamento da página
     * 
     * @return Retorna o layout com o fragmento de atualização, ou retorna para
     *         listagem de clientes em caso de erro
     */
    @GetMapping("/editar/{id}")
    public String editarClienteForm(@PathVariable String id, Model model,
            RedirectAttributes redirectAttributes) {
        boolean encontrado = clienteService.buscarPorId(id).map(c -> {
            model.addAttribute("cliente", c);
            return true;
        }).orElse(false);

        if (!encontrado) {
            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado.");
            return "redirect:/clientes";
        }

        model.addAttribute("content", "clientes/form :: content");
        return "layout";
    }

    /**
     * Endpoint para atualização de clientes
     * 
     * @param id Identificador do cliente
     * @param cliente Objeto com dados a serem atualizados 
     * @param redirectAttributes Objeto para redirecionamento de usuários
     * 
     * @return Redireciona para página de clientes quando a atualização é bem-sucedida
     */
    @PostMapping("/editar/{id}")
    public String atualizarCliente(@PathVariable String id,
            @ModelAttribute ClienteRegisterDTO dto,
            RedirectAttributes redirectAttributes) {
        clienteService.salvar(dto.toModel());
        redirectAttributes.addFlashAttribute("sucesso", "Cliente atualizado com sucesso!");
        return "redirect:/clientes";
    }

    /**
     * Endpoint para remoção de clientes
     * @param id Identificador do cliente
     * @param redirectAttributes Objeto para redirecionamento do usuário 
     * @return Redireciona o usuário para página de clientes quando a exclusão é bem-sucedida
     */
    @PostMapping("/deletar/{id}")
    public String deletarCliente(@PathVariable String id, RedirectAttributes redirectAttributes) {
        clienteService.excluir(id);
        redirectAttributes.addFlashAttribute("sucesso", "Cliente removido com sucesso!");
        return "redirect:/clientes";
    }

}
