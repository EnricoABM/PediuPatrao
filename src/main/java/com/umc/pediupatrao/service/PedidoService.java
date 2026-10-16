package com.umc.pediupatrao.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.umc.pediupatrao.dto.PedidoRegisterDTO;
import com.umc.pediupatrao.entity.pedido.ItemPedido;
import com.umc.pediupatrao.entity.pedido.Pedido;
import com.umc.pediupatrao.entity.pedido.PedidoHistorico;
import com.umc.pediupatrao.entity.pedido.PedidoStatus;
import com.umc.pediupatrao.entity.user.Usuario;
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

    private final PedidoMapper pedidoMapper;

    PedidoService(PedidoRepository pedidoRepository, ItemPedidoRepository itemRepository,
            PedidoHistoricoRepository historicoRepository, UsuarioService usuarioService, PedidoMapper pedidoMapper) {
        this.pedidoRepository = pedidoRepository;
        this.itemRepository = itemRepository;
        this.historicoRepository = historicoRepository;
        this.usuarioService = usuarioService;
        this.pedidoMapper = pedidoMapper;
    }

    @PreAuthorize("hasAuthority('ORDER_CREATE')")
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

    @PreAuthorize("hasAuthority('ORDER_READ_ALL')")
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    @PreAuthorize("hasAuthority('ORDER_READ')")
    public Pedido buscarPorId(String id) {
        return pedidoRepository.findById(id).orElseThrow();
    }

    @PreAuthorize("hasAuthority('ORDER_PROGRESS')")
    public void avancarStatus(String pedidoId, String usuarioNome) {
        Usuario usuario = usuarioService.buscarPorUsername(usuarioNome).orElseThrow();
        Pedido pedido = buscarPorId(pedidoId);
        
        if (pedido.getStatus() == PedidoStatus.FINALIZADO)
            return;

        PedidoStatus estadoAnterior = pedido.getStatus();
        PedidoStatus proximoEstado = passarEstado(estadoAnterior);

        pedido.setStatus(proximoEstado);

        registrarHistorico(pedido, usuario, estadoAnterior, proximoEstado);
        pedidoRepository.save(pedido);
    }

    @PreAuthorize("hasAuthority('ORDER_DELETE')")
    public void cancelarPedido(String pedidoId, String justificativa, String usuarioNome) {
        Usuario usuario = usuarioService.buscarPorUsername(usuarioNome).orElseThrow();
        Pedido pedido = buscarPorId(pedidoId);
        
        PedidoStatus estado = pedido.getStatus();

        if(estado == PedidoStatus.FINALIZADO || estado == PedidoStatus.RETIRADO || estado == PedidoStatus.SAIU_PARA_ENTREGA || estado == PedidoStatus.CANCELADO)
            throw new RuntimeException("O pedido não pode ser cancelado");

        pedido.setStatus(PedidoStatus.CANCELADO);

        pedido.setJustificativa(justificativa);
        registrarHistorico(pedido, usuario, estado, PedidoStatus.CANCELADO);
        pedidoRepository.save(pedido);
    }

    @PreAuthorize("hasAuthority('ORDER_DESCOUNT')")
    public void aplicarDesconto(String pedidoId, Integer desconto) {
        Pedido pedido = buscarPorId(pedidoId);
        pedido.setDesconto(desconto);
        
        pedido.setTotal(this.calcularTotal(pedido));
        
        pedidoRepository.save(pedido);
    }

    private PedidoStatus passarEstado(PedidoStatus atual) {
        switch (atual) {
            case RECEBIDO: 
                return PedidoStatus.EM_PREPARACAO;
            case EM_PREPARACAO: 
                return PedidoStatus.PRONTO;
            case PRONTO: 
                return PedidoStatus.RETIRADO;
            case RETIRADO:
                return PedidoStatus.FINALIZADO;
            default: return atual;
        }
    }

    private PedidoHistorico registrarHistorico(Pedido pedido, Usuario usuario, PedidoStatus anterior, PedidoStatus atual) {
        PedidoHistorico historico = PedidoHistorico.builder()
                .id(UUID.randomUUID().toString())
                .pedido(pedido)
                .usuario(usuario)
                .horario(LocalDateTime.now())
                .estadoAnterior(anterior)
                .estadoAtual(atual)
                .build();

        pedido.getHistoricos().add(historico);
        
        return historicoRepository.save(historico);
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
