package com.umc.pediupatrao.entity.pedido;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.umc.pediupatrao.entity.Cliente;
import com.umc.pediupatrao.entity.user.Usuario;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "pedidos")
public class Pedido {
    @Id
    private String id;

    @DBRef(lazy = true)
    private Cliente cliente;

    @DBRef(lazy = true)
    private Usuario usuario;

    @CreatedDate
    private LocalDateTime horario;

    @Enumerated(EnumType.STRING)
    private PedidoStatus status;

    private Integer desconto;

    private Double subtotal;

    private Double total;

    @DBRef(lazy = true)
    private List<ItemPedido> itens;

    @DBRef(lazy = true)
    private List<PedidoHistorico> historicos = new ArrayList<>();

    private String justificativa;

    public static PedidoBuilder builder() {
        return new PedidoBuilder();
    }

    public static class PedidoBuilder {
        private String id;
        private Cliente cliente;
        private Usuario usuario;
        private LocalDateTime horario;
        private PedidoStatus status;
        private Integer desconto;
        private Double subtotal;
        private Double total;
        private List<ItemPedido> itens;
        private List<PedidoHistorico> historicos;
        private String justificativa;

        public PedidoBuilder id(String id) {
            this.id = id;
            return this;
        }

        public PedidoBuilder cliente(Cliente cliente) {
            this.cliente = cliente;
            return this;
        }

        public PedidoBuilder usuario(Usuario usuario) {
            this.usuario = usuario;
            return this;
        }

        public PedidoBuilder horario(LocalDateTime horario) {
            this.horario = horario;
            return this;
        }

        public PedidoBuilder status(PedidoStatus status) {
            this.status = status;
            return this;
        }

        public PedidoBuilder desconto(Integer desconto) {
            this.desconto = desconto;
            return this;
        }

        public PedidoBuilder subtotal(Double subtotal) {
            this.subtotal = subtotal;
            return this;
        }

        public PedidoBuilder total(Double total) {
            this.total = total;
            return this;
        }

        public PedidoBuilder itens(List<ItemPedido> itens) {
            this.itens = itens;
            return this;
        }

        public PedidoBuilder historicos(List<PedidoHistorico> historicos) {
            this.historicos = historicos;
            return this;
        }

        public PedidoBuilder justificativa(String justificativa) {
            this.justificativa = justificativa;
            return this;
        }

        public Pedido build() {
            Pedido ped = new Pedido();
            ped.setId(id);
            ped.setCliente(cliente);
            ped.setUsuario(usuario);
            ped.setHorario(horario);
            ped.setStatus(status);
            ped.setDesconto(desconto);
            ped.setSubtotal(subtotal);
            ped.setTotal(total);
            ped.setItens(itens);
            ped.setHistoricos(historicos);
            ped.setJustificativa(justificativa);

            return ped;
        }
    }
}
