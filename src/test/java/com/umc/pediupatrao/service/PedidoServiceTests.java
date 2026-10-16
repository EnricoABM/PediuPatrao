package com.umc.pediupatrao.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.umc.pediupatrao.dto.ItemPedidoDTO;
import com.umc.pediupatrao.dto.PedidoRegisterDTO;
import com.umc.pediupatrao.entity.Produto;
import com.umc.pediupatrao.entity.pedido.ItemPedido;
import com.umc.pediupatrao.entity.pedido.Pedido;
import com.umc.pediupatrao.entity.pedido.PedidoHistorico;
import com.umc.pediupatrao.entity.pedido.PedidoStatus;
import com.umc.pediupatrao.entity.user.Usuario;
import com.umc.pediupatrao.repository.pedido.ItemPedidoRepository;
import com.umc.pediupatrao.repository.pedido.PedidoHistoricoRepository;
import com.umc.pediupatrao.repository.pedido.PedidoRepository;
import com.umc.pediupatrao.utils.PedidoMapper;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTests {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ItemPedidoRepository itemRepository;

    @Mock
    private PedidoHistoricoRepository historicoRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private PedidoMapper pedidoMapper;

    @InjectMocks
    private PedidoService pedidoService;

    @Mock 
    private AuditoriaService auditoriaService;

    @Test
    void deveCriarPedidoComSucesso() {
        // Arrange
        PedidoRegisterDTO dto = getPedidoRegisterDTOTest();
        Pedido pedido = getPedidoTest();
        
        when(pedidoMapper.conveterDTO(dto)).thenReturn(pedido);
        when(historicoRepository.save(any(PedidoHistorico.class))).thenReturn(new PedidoHistorico());
        when(itemRepository.saveAll(any())).thenReturn(new ArrayList<>());
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // Act e Assert
        Pedido resultado = pedidoService.criarPedido(dto);

        assertNotNull(resultado);
        assertEquals(90.00, resultado.getSubtotal());
        assertEquals(90.00, resultado.getTotal());
        verify(pedidoMapper, times(1)).conveterDTO(dto);
        verify(historicoRepository, times(1)).save(any(PedidoHistorico.class));
        verify(itemRepository, times(1)).saveAll(any());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void deveListarTodosOsPedidosComSucesso() {
        // Arrange
        Pedido pedido = getPedidoTest();
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));

        // Act e Assert
        List<Pedido> resultado = pedidoService.listarPedidos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("ped-123", resultado.get(0).getId());
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void deveBuscarPedidoPorIdComSucesso() {
        // Arrange
        Pedido pedido = getPedidoTest();
        when(pedidoRepository.findById("ped-123")).thenReturn(Optional.of(pedido));

        // Act e Assert
        Pedido resultado = pedidoService.buscarPorId("ped-123");

        assertNotNull(resultado);
        assertEquals("ped-123", resultado.getId());
        verify(pedidoRepository, times(1)).findById("ped-123");
    }

    @Test
    void deveAvancarStatusPedidoComSucesso() {
        // Arrange
        Pedido pedido = getPedidoTest();
        pedido.setStatus(PedidoStatus.RECEBIDO);
        Usuario usuario = getUsuarioTest();

        when(usuarioService.buscarPorUsername("admin")).thenReturn(Optional.of(usuario));
        when(pedidoRepository.findById("ped-123")).thenReturn(Optional.of(pedido));
        when(historicoRepository.save(any(PedidoHistorico.class))).thenReturn(new PedidoHistorico());
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // Act e Assert
        pedidoService.avancarStatus("ped-123", "admin");

        assertEquals(PedidoStatus.EM_PREPARACAO, pedido.getStatus());
        verify(usuarioService, times(1)).buscarPorUsername("admin");
        verify(pedidoRepository, times(1)).findById("ped-123");
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void deveCancelarPedidoComSucesso() {
        // Arrange
        Pedido pedido = getPedidoTest();
        pedido.setStatus(PedidoStatus.RECEBIDO);
        Usuario usuario = getUsuarioTest();

        when(usuarioService.buscarPorUsername("admin")).thenReturn(Optional.of(usuario));
        when(pedidoRepository.findById("ped-123")).thenReturn(Optional.of(pedido));
        when(historicoRepository.save(any(PedidoHistorico.class))).thenReturn(new PedidoHistorico());
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // Act e Assert
        pedidoService.cancelarPedido("ped-123", "Cliente desistiu", "admin");

        assertEquals(PedidoStatus.CANCELADO, pedido.getStatus());
        assertEquals("Cliente desistiu", pedido.getJustificativa());
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void deveLancarExcecaoAoTentarCancelarPedidoFinalizado() {
        // Arrange
        Pedido pedido = getPedidoTest();
        pedido.setStatus(PedidoStatus.FINALIZADO);
        Usuario usuario = getUsuarioTest();

        when(usuarioService.buscarPorUsername("admin")).thenReturn(Optional.of(usuario));
        when(pedidoRepository.findById("ped-123")).thenReturn(Optional.of(pedido));

        // Act e Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pedidoService.cancelarPedido("ped-123", "Tentativa tardia", "admin");
        });

        assertEquals("O pedido não pode ser cancelado", exception.getMessage());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void deveAplicarDescontoComSucesso() {
        // Arrange
        Pedido pedido = getPedidoTest();
        pedido.setSubtotal(100.00);
        pedido.setDesconto(0);

        when(pedidoRepository.findById("ped-123")).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // Act e Assert
        pedidoService.aplicarDesconto("ped-123", 10);

        assertEquals(10, pedido.getDesconto());
        assertEquals(90.00, pedido.getTotal());
        verify(pedidoRepository, times(1)).save(pedido);
    }

    private Pedido getPedidoTest() {
        Pedido pedido = new Pedido();
        pedido.setId("ped-123");
        pedido.setStatus(PedidoStatus.RECEBIDO);
        pedido.setHistoricos(new ArrayList<>());
        
        Produto prod = new Produto();
        prod.setId("prod-123");
        prod.setNome("Pizza Margherita");
        prod.setPreco(45.00);

        ItemPedido item = new ItemPedido();
        item.setId("item-123");
        item.setProduto(prod);
        item.setQuantidade(2);

        pedido.setItens(List.of(item));
        return pedido;
    }

    private PedidoRegisterDTO getPedidoRegisterDTOTest() {
        ItemPedidoDTO itemDto = new ItemPedidoDTO("prod-123", "Pizza Margherita", 2);
        return new PedidoRegisterDTO("cli-123", "user-123", List.of(itemDto));
    }

    private Usuario getUsuarioTest() {
        Usuario usuario = new Usuario();
        usuario.setId("user-123");
        usuario.setUsername("admin");
        return usuario;
    }
}