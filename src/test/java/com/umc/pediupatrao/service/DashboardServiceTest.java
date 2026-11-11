package com.umc.pediupatrao.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.umc.pediupatrao.dto.DashboardFiltro;
import com.umc.pediupatrao.entity.Produto;
import com.umc.pediupatrao.entity.pedido.ItemPedido;
import com.umc.pediupatrao.entity.pedido.Pedido;
import com.umc.pediupatrao.entity.pedido.PedidoHistorico;
import com.umc.pediupatrao.entity.pedido.PedidoStatus;
import com.umc.pediupatrao.repository.DashboardRepository;
import com.umc.pediupatrao.repository.pedido.PedidoRepository;

class DashboardServiceTest {
    private DashboardRepository repository;
    private PedidoRepository pedidoRepository;
    private DashboardService service;
    private final LocalDate dia = LocalDate.of(2026, 11, 10);
    private final DashboardFiltro filtro = new DashboardFiltro(dia, dia, null);

    @BeforeEach
    void setup() {
        repository = mock(DashboardRepository.class);
        pedidoRepository = mock(PedidoRepository.class);
        service = new DashboardService(repository, pedidoRepository);
    }

    private Pedido pedido(PedidoStatus status, double total, String id) {
        Pedido p = new Pedido();
        p.setId(id);
        p.setStatus(status);
        p.setTotal(total);
        p.setHorario(dia.atTime(12, 0));
        return p;
    }

    @Test
    void resumoContaSomenteFinalizadosNoFaturamento() {
        when(repository.pedidosCriadosEntre(any(), any())).thenReturn(List.of(
                pedido(PedidoStatus.FINALIZADO, 95, "1"),
                pedido(PedidoStatus.FINALIZADO, 55, "2"),
                pedido(PedidoStatus.CANCELADO, 200, "3")));
        var r = service.resumo(filtro);
        assertEquals(new BigDecimal("150.00"), r.faturamento());
        assertEquals(2, r.pedidosFinalizados());
        assertEquals(1, r.pedidosCancelados());
        assertEquals(new BigDecimal("75.00"), r.ticketMedio());
    }

    @Test
    void ticketMedioZeroSemPedidos() {
        when(repository.pedidosCriadosEntre(any(), any())).thenReturn(List.of());
        var r = service.resumo(filtro);
        assertEquals(new BigDecimal("0.00"), r.ticketMedio());
        assertEquals(0, r.pedidosFinalizados());
    }

    @Test
    void rankingSomaQuantidadesEIgnoraCancelados() {
        Produto produto = new Produto();
        produto.setId("p1");
        produto.setNome("Pizza");
        Pedido a = pedido(PedidoStatus.FINALIZADO, 100, "1");
        Pedido b = pedido(PedidoStatus.FINALIZADO, 90, "2");
        Pedido c = pedido(PedidoStatus.CANCELADO, 120, "3");
        a.setItens(List.of(item(produto, 2)));
        b.setItens(List.of(item(produto, 3)));
        c.setItens(List.of(item(produto, 9)));
        when(repository.pedidosCriadosEntre(any(), any())).thenReturn(List.of(a, b, c));
        var ranking = service.ranking(filtro, 5);
        assertEquals(1, ranking.size());
        assertEquals(5, ranking.get(0).quantidadeVendida());
    }

    @Test
    void transicaoUsaHorarioInicialEEventoDentroDoPeriodo() {
        Pedido p = pedido(PedidoStatus.FINALIZADO, 100, "1");
        p.setHorario(dia.atTime(10, 0));
        PedidoHistorico h = new PedidoHistorico();
        h.setPedido(p);
        h.setHorario(dia.atTime(10, 30));
        h.setEstadoAnterior(PedidoStatus.RECEBIDO);
        h.setEstadoAtual(PedidoStatus.EM_PREPARACAO);
        when(repository.historicosAte(any())).thenReturn(List.of(h));
        when(pedidoRepository.findById("1")).thenReturn(java.util.Optional.of(p));
        var tempos = service.tempos(filtro);
        assertEquals(1, tempos.size());
        assertEquals(30.0, tempos.get(0).mediaMinutos());
    }

    private ItemPedido item(Produto produto, int quantidade) {
        ItemPedido i = new ItemPedido();
        i.setProduto(produto);
        i.setQuantidade(quantidade);
        return i;
    }
}
