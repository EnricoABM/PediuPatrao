package com.umc.pediupatrao.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.umc.pediupatrao.dto.DashboardFiltro;
import com.umc.pediupatrao.dto.DashboardResumoDTO;
import com.umc.pediupatrao.dto.ProdutoRankingDTO;
import com.umc.pediupatrao.dto.StatusPedidoDTO;
import com.umc.pediupatrao.dto.TempoTransicaoDTO;
import com.umc.pediupatrao.dto.VendaDiariaDTO;
import com.umc.pediupatrao.entity.Produto;
import com.umc.pediupatrao.entity.pedido.ItemPedido;
import com.umc.pediupatrao.entity.pedido.Pedido;
import com.umc.pediupatrao.entity.pedido.PedidoHistorico;
import com.umc.pediupatrao.entity.pedido.PedidoStatus;
import com.umc.pediupatrao.repository.DashboardRepository;
import com.umc.pediupatrao.repository.pedido.PedidoRepository;

@Service
@PreAuthorize("hasRole('GERENTE')")
public class DashboardService {
    private final DashboardRepository repository;
    private final PedidoRepository pedidoRepository;

    public DashboardService(DashboardRepository repository, PedidoRepository pedidoRepository) {
        this.repository = repository;
        this.pedidoRepository = pedidoRepository;
    }

    private List<Pedido> pedidos(DashboardFiltro filtro) {
        return repository.pedidosCriadosEntre(filtro.inicio().atStartOfDay(),
                filtro.fim().plusDays(1).atStartOfDay()).stream()
                .filter(p -> possuiProduto(p, filtro.produtoId()))
                .toList();
    }

    private boolean possuiProduto(Pedido pedido, String produtoId) {
        if (produtoId == null)
            return true;
        return itens(pedido).stream().anyMatch(item -> item.getProduto() != null
                && produtoId.equals(item.getProduto().getId()));
    }

    private List<ItemPedido> itens(Pedido pedido) {
        return pedido.getItens() == null ? List.of()
                : pedido.getItens().stream()
                        .filter(Objects::nonNull).toList();
    }

    private BigDecimal valor(Pedido pedido) {
        return pedido.getTotal() == null ? BigDecimal.ZERO
                : BigDecimal.valueOf(pedido.getTotal());
    }

    private BigDecimal moeda(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    public DashboardResumoDTO resumo(DashboardFiltro filtro) {
        List<Pedido> pedidos = pedidos(filtro);
        long finalizados = pedidos.stream().filter(p -> p.getStatus() == PedidoStatus.FINALIZADO).count();
        long cancelados = pedidos.stream().filter(p -> p.getStatus() == PedidoStatus.CANCELADO).count();
        BigDecimal faturamento = pedidos.stream()
                .filter(p -> p.getStatus() == PedidoStatus.FINALIZADO)
                .map(this::valor).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal ticket = finalizados == 0 ? BigDecimal.ZERO
                : faturamento.divide(BigDecimal.valueOf(finalizados), 2, RoundingMode.HALF_UP);
        return new DashboardResumoDTO(moeda(faturamento), finalizados, cancelados, moeda(ticket));
    }

    public List<VendaDiariaDTO> vendas(DashboardFiltro filtro) {
        Map<LocalDate, List<Pedido>> porDia = pedidos(filtro).stream()
                .filter(p -> p.getStatus() == PedidoStatus.FINALIZADO)
                .collect(Collectors.groupingBy(p -> p.getHorario().toLocalDate()));
        List<VendaDiariaDTO> resultado = new ArrayList<>();
        for (LocalDate dia = filtro.inicio(); !dia.isAfter(filtro.fim()); dia = dia.plusDays(1)) {
            List<Pedido> lista = porDia.getOrDefault(dia, List.of());
            BigDecimal total = lista.stream().map(this::valor).reduce(BigDecimal.ZERO, BigDecimal::add);
            resultado.add(new VendaDiariaDTO(dia, moeda(total), lista.size()));
        }
        return resultado;
    }

    public List<StatusPedidoDTO> status(DashboardFiltro filtro) {
        Map<PedidoStatus, Long> contagem = pedidos(filtro).stream()
                .filter(p -> p.getStatus() != null)
                .collect(Collectors.groupingBy(Pedido::getStatus, () -> new EnumMap<>(PedidoStatus.class),
                        Collectors.counting()));
        return Arrays.stream(PedidoStatus.values())
                .map(status -> new StatusPedidoDTO(status, contagem.getOrDefault(status, 0L))).toList();
    }

    public List<ProdutoRankingDTO> ranking(DashboardFiltro filtro, int limite) {
        Map<String, Long> quantidades = new HashMap<>();
        Map<String, String> nomes = new HashMap<>();
        for (Pedido pedido : pedidos(filtro)) {
            if (pedido.getStatus() != PedidoStatus.FINALIZADO)
                continue;
            for (ItemPedido item : itens(pedido)) {
                Produto produto = item.getProduto();
                if (produto == null || produto.getId() == null || item.getQuantidade() == null)
                    continue;
                String id = produto.getId();
                if (filtro.produtoId() != null && !filtro.produtoId().equals(id))
                    continue;
                quantidades.merge(id, item.getQuantidade().longValue(), Long::sum);
                nomes.put(id, produto.getNome());
            }
        }
        return quantidades.entrySet().stream()
                .map(e -> new ProdutoRankingDTO(e.getKey(), nomes.get(e.getKey()), e.getValue()))
                .sorted(Comparator.comparingLong(ProdutoRankingDTO::quantidadeVendida).reversed()
                        .thenComparing(ProdutoRankingDTO::produtoId))
                .limit(limite).toList();
    }

    public List<TempoTransicaoDTO> tempos(DashboardFiltro filtro) {
        LocalDateTime inicio = filtro.inicio().atStartOfDay();
        LocalDateTime fimExclusivo = filtro.fim().plusDays(1).atStartOfDay();
       
        Map<String, List<PedidoHistorico>> porPedido = new HashMap<>();
        for (PedidoHistorico h : repository.historicosAte(fimExclusivo)) {
            if (h.getHorario() == null || h.getPedido() == null || h.getPedido().getId() == null)
                continue;
            porPedido.computeIfAbsent(h.getPedido().getId(), k -> new ArrayList<>()).add(h);
        }

        record Transicao(PedidoStatus anterior, PedidoStatus atual) {
        }
        Map<Transicao, long[]> acumulados = new LinkedHashMap<>();
        for (var entry : porPedido.entrySet()) {
            Pedido pedido = pedidoRepository.findById(entry.getKey()).orElse(null);
            if (pedido == null || !possuiProduto(pedido, filtro.produtoId()))
                continue;
            List<PedidoHistorico> historicos = entry.getValue();
            historicos.sort(Comparator.comparing(PedidoHistorico::getHorario));
            LocalDateTime instanteAnterior = pedido.getHorario();
            PedidoStatus estadoAnterior = PedidoStatus.RECEBIDO;
            for (PedidoHistorico h : historicos) {
                LocalDateTime instante = h.getHorario();
               
                if (h.getEstadoAnterior() == null) {
                    instanteAnterior = instante;
                    estadoAnterior = h.getEstadoAtual();
                    continue;
                }
                if (instanteAnterior != null && !instante.isBefore(instanteAnterior)
                        && !instante.isBefore(inicio) && instante.isBefore(fimExclusivo)
                        && h.getEstadoAnterior() == estadoAnterior && h.getEstadoAtual() != null) {
                    Transicao transicao = new Transicao(h.getEstadoAnterior(), h.getEstadoAtual());
                    long[] stats = acumulados.computeIfAbsent(transicao, k -> new long[2]);
                    stats[0] += Duration.between(instanteAnterior, instante).getSeconds();
                    stats[1]++;
                }
                instanteAnterior = instante;
                estadoAnterior = h.getEstadoAtual();
            }
        }
        return acumulados.entrySet().stream()
                .map(e -> new TempoTransicaoDTO(e.getKey().anterior(), e.getKey().atual(),
                        Math.round((e.getValue()[0] / 60.0 / e.getValue()[1]) * 100.0) / 100.0,
                        e.getValue()[1]))
                .sorted(Comparator.comparing((TempoTransicaoDTO t) -> t.estadoAnterior().ordinal())
                        .thenComparing(t -> t.estadoAtual().ordinal()))
                .toList();
    }

    public DashboardFiltro criarFiltro(Integer periodo, LocalDate inicio, LocalDate fim, String produtoId) {
        LocalDate hoje = LocalDate.now();


        if (inicio == null) {
            int dias = periodo == null ? 30 : periodo;

            if (dias != 7 && dias != 30 && dias != 90) {
                throw new IllegalArgumentException("Período permitido: 7, 30 ou 90 dias.");
            }
            fim = hoje;
            inicio = hoje.minusDays(dias - 1L);
        }

        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("A data inicial não pode ser posterior à data final.");
        }

        if (fim.isAfter(hoje)) {
            throw new IllegalArgumentException("A data final não pode estar no futuro.");
        }

        return new DashboardFiltro(inicio, fim, produtoId == null || produtoId.isBlank() ? null : produtoId);
    }
}
