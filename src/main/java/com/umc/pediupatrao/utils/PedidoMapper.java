package com.umc.pediupatrao.utils;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.umc.pediupatrao.dto.ItemPedidoDTO;
import com.umc.pediupatrao.dto.PedidoRegisterDTO;
import com.umc.pediupatrao.entity.Cliente;
import com.umc.pediupatrao.entity.Produto;
import com.umc.pediupatrao.entity.pedido.ItemPedido;
import com.umc.pediupatrao.entity.pedido.Pedido;
import com.umc.pediupatrao.entity.pedido.PedidoStatus;
import com.umc.pediupatrao.entity.user.Usuario;
import com.umc.pediupatrao.repository.ClienteRepository;
import com.umc.pediupatrao.repository.ProdutoRepository;
import com.umc.pediupatrao.repository.UsuarioRepository;

@Component
public class PedidoMapper {

    private ClienteRepository clienteRepository;

    private ProdutoRepository produtoRepository;

    private UsuarioRepository usuarioRepository;

    PedidoMapper(ClienteRepository clienteRepository, ProdutoRepository produtoRepository,
            UsuarioRepository usuarioRepository) {
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Pedido conveterDTO(PedidoRegisterDTO pedidoDto) {
        Cliente cliente = clienteRepository.findById(pedidoDto.clienteId()).orElseThrow();
        Usuario criador = usuarioRepository.findById(pedidoDto.usuarioId()).orElseThrow();

        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .usuario(criador)
                .itens(
                        pedidoDto.itens().stream().map(this::criarItem).toList())
                .status(PedidoStatus.RECEBIDO)
                .horario(LocalDateTime.now())
                .build();

        return pedido;
    }

    private ItemPedido criarItem(ItemPedidoDTO dto) {
        if (dto.produtoId() == null || dto.produtoId().isBlank()) {
            throw new IllegalArgumentException("O ID do produto não pode ser nulo ou vazio.");
        }

        Produto prod = produtoRepository.findById(dto.produtoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + dto.produtoId()));

        ItemPedido item = ItemPedido.builder()
                .produto(prod)
                .quantidade(dto.quantidade())
                .build();

        return item;
    }
}
