package com.umc.pediupatrao.controller;

import java.security.Principal;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.umc.pediupatrao.dto.ClienteInfoDTO;
import com.umc.pediupatrao.dto.PedidoListInfoDTO;
import com.umc.pediupatrao.dto.PedidoRegisterDTO;
import com.umc.pediupatrao.dto.ProdutoInfoDTO;
import com.umc.pediupatrao.entity.pedido.Pedido;
import com.umc.pediupatrao.entity.user.Usuario;
import com.umc.pediupatrao.repository.UsuarioRepository;
import com.umc.pediupatrao.service.ClienteService;
import com.umc.pediupatrao.service.PedidoService;
import com.umc.pediupatrao.service.ProdutoService;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    private final ClienteService clienteService;

    private final ProdutoService produtoService;

    private final UsuarioRepository usuarioRepository;

    private static Logger log = LoggerFactory.getLogger(PedidoController.class);

    PedidoController(PedidoService pedidoService, ClienteService clienteService, ProdutoService produtoService,
            UsuarioRepository usuarioRepository) {
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
        this.produtoService = produtoService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String pedidos(Model model) {
        List<PedidoListInfoDTO> pedidos = pedidoService.listarPedidos().stream().map(PedidoListInfoDTO::from).toList();
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("content", "pedidos/lista :: content");
        log.info("Carregando fragmento: pedidos :: content");
        return "layout";
    }

    @GetMapping("/novo")
    public String novoPedidoForm(Model model, Principal principal) {
        model.addAttribute("pedido", new PedidoRegisterDTO(null, null, null));

        Usuario usuario = usuarioRepository.findByUsername(principal.getName()).orElseThrow();

        model.addAttribute("usuario", usuario);

        List<ClienteInfoDTO> clientes = clienteService.listarClientes()
                .stream().map(ClienteInfoDTO::fromModel)
                .toList();

        model.addAttribute("clientes", clientes);

        List<ProdutoInfoDTO> produtos = produtoService.listarProdutos()
                .stream().map(ProdutoInfoDTO::from)
                .toList();

        model.addAttribute("produtos", produtos);

        model.addAttribute("content", "pedidos/form :: content");
        return "layout";
    }

    @PostMapping
    public String criarPedido(@ModelAttribute PedidoRegisterDTO dto, RedirectAttributes redirectAttributes) {
        pedidoService.criarPedido(dto);
        redirectAttributes.addFlashAttribute("sucesso", "Pedido cadastrado com sucesso!");
        return "redirect:/pedidos";
    }

    @GetMapping("/exibir/{id}")
    public String exibirDetalhesPedido(@PathVariable String id, Model model) {
        Pedido pedido = pedidoService.buscarPorId(id);
        model.addAttribute("pedido", pedido);
        model.addAttribute("content", "pedidos/info :: content");
        return "layout";
    }

    @PostMapping("/{id}/avancar")
    public String avancarPedido(@PathVariable String id, Principal principal) {
        pedidoService.avancarStatus(id, principal.getName());
        return "redirect:/pedidos/exibir/" + id;
    }

    @PostMapping("/{id}/cancelar")
    public String cancelarPedido(@PathVariable String id, @RequestParam String justificativa, Principal principal) {
        pedidoService.cancelarPedido(id, justificativa, principal.getName());
        log.info("Cancelando pedido");
        return "redirect:/pedidos/exibir/" + id;
    }

    @PostMapping("/{id}/desconto")
public String aplicarDesconto(@PathVariable String id, @RequestParam Integer desconto) {
    pedidoService.aplicarDesconto(id, desconto);
    return "redirect:/pedidos/exibir/" + id;
}

}
