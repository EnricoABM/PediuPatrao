package com.umc.pediupatrao.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.umc.pediupatrao.dto.PedidoRegisterDTO;
import com.umc.pediupatrao.entity.pedido.ItemPedido;
import com.umc.pediupatrao.entity.pedido.Pedido;
import com.umc.pediupatrao.entity.pedido.PedidoHistorico;
import com.umc.pediupatrao.repository.pedido.ItemPedidoRepository;
import com.umc.pediupatrao.repository.pedido.PedidoHistoricoRepository;
import com.umc.pediupatrao.repository.pedido.PedidoRepository;
import com.umc.pediupatrao.utils.PedidoMapper;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    private final ItemPedidoRepository itemRepository;

    private final PedidoHistoricoRepository historicoRepository;

    private final UsuarioService usuarioService;

    private final ProdutoService produtoService;

    private final ClienteService clienteService;

    private final PedidoMapper pedidoMapper;

    PedidoService(PedidoRepository pedidoRepository, ItemPedidoRepository itemRepository,
            PedidoHistoricoRepository historicoRepository, ProdutoService produtoService, UsuarioService usuarioService, ClienteService clienteService, PedidoMapper pedidoMapper) {
        this.pedidoRepository = pedidoRepository;
        this.itemRepository = itemRepository;
        this.historicoRepository = historicoRepository;
        this.usuarioService = usuarioService;
        this.produtoService = produtoService;
        this.clienteService = clienteService;
        this.pedidoMapper = pedidoMapper;
    }

    public Pedido criarPedido(PedidoRegisterDTO dto) {
        Pedido pedido = pedidoMapper.conveterDTO(dto);

        pedido.setSubtotal(this.calcularSubtotal(pedido));
        pedido.setTotal(this.calcularTotal(pedido));

        pedido.setId(UUID.randomUUID().toString());
        List<ItemPedido> itens = pedido
                .getItens()
                .stream().map((ItemPedido item) -> {
                    item.setId(UUID.randomUUID().toString());
                    item.setPedido(pedido);
                    return item;
                }).toList();

        PedidoHistorico historico = new PedidoHistorico();
        historico.setId(UUID.randomUUID().toString());
        historico.setPedido(pedido);
        historico.setEstadoAnterior(null);
        historico.setEstadoAtual(pedido.getStatus());

        historicoRepository.save(historico);
        itemRepository.saveAll(itens);
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public Pedido atualizarStatus(String id, String status) {
        return null;
    }

    private Double calcularSubtotal(Pedido ped) {
        List<ItemPedido> itens = ped.getItens();

        Double subtotal = 0.0;

        for (ItemPedido item : itens) {
            subtotal += item.getProduto().getPreco() * item.getQuantidade();
        }

        return subtotal;
    }

    private Double calcularTotal(Pedido ped) {
        if (ped.getDesconto() == null) 
            return ped.getSubtotal();
        Double descontoPorcentagem = (double) ped.getDesconto() / 100;
        Double total = ped.getSubtotal() * (1 - descontoPorcentagem);
        return total;
    }

}
