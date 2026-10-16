package com.umc.pediupatrao.entity.pedido;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.umc.pediupatrao.entity.Produto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "item_pedido")
public class ItemPedido {

    @Id
    private String id;

    @DBRef(lazy = true)
    private Produto produto;

    @DBRef(lazy = true)
    private Pedido pedido;

    private Integer quantidade;

    public static ItemPedidoBuilder builder() {
        return new ItemPedidoBuilder();
    }

    public static class ItemPedidoBuilder {
        private String id;
        private Produto produto;
        private Pedido pedido;
        private Integer quantidade;

        public ItemPedidoBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ItemPedidoBuilder produto(Produto produto) {
            this.produto = produto;
            return this;
        }

        public ItemPedidoBuilder pedido(Pedido pedido) {
            this.pedido = pedido;
            return this;
        }

        public ItemPedidoBuilder quantidade(Integer quantidade) {
            this.quantidade = quantidade;
            return this;
        }

        public ItemPedido build() {
            ItemPedido item = new ItemPedido();
            item.setId(id);
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(quantidade);

            return item;
        }
    };

}
