package com.umc.pediupatrao.entity.pedido;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.umc.pediupatrao.entity.user.Usuario;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "pedido_historico")
public class PedidoHistorico {

    @Id
    private String id;

    @DBRef(lazy = true)
    private Pedido pedido;

    @DBRef(lazy = true)
    private Usuario usuario;

    @CreatedDate
    private LocalDateTime horario;
    private PedidoStatus estadoAnterior;
    private PedidoStatus estadoAtual;

    public static PedidoHistoricoBuilder builder() {
        return new PedidoHistoricoBuilder();
    }

    public static class PedidoHistoricoBuilder {
        private String id;
        private Pedido pedido;
        private Usuario usuario;
        private LocalDateTime horario;
        private PedidoStatus estadoAnterior;
        private PedidoStatus estadoAtual;

        public PedidoHistoricoBuilder id(String id) {
            this.id = id;
            return this;
        }

        public PedidoHistoricoBuilder pedido(Pedido pedido) {
            this.pedido = pedido;
            return this;
        }

        public PedidoHistoricoBuilder usuario(Usuario usuario) {
            this.usuario = usuario;
            return this;
        }

        public PedidoHistoricoBuilder horario(LocalDateTime horario) {
            this.horario = horario;
            return this;
        }

        public PedidoHistoricoBuilder estadoAnterior(PedidoStatus estadoAnterior) {
            this.estadoAnterior = estadoAnterior;
            return this;
        }

        public PedidoHistoricoBuilder estadoAtual(PedidoStatus estadoAtual) {
            this.estadoAtual = estadoAtual;
            return this;
        }

        public PedidoHistorico build() {
            PedidoHistorico historico = new PedidoHistorico();
            historico.setId(id);
            historico.setPedido(pedido);
            historico.setUsuario(usuario);
            historico.setHorario(horario);
            historico.setEstadoAtual(estadoAtual);
            historico.setEstadoAnterior(estadoAnterior);

            return historico;
        }
    }
}
