package br.com.cultivae.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Tabela item_pedido_safra (relacao N:N entre pedido e safra).
 * O preco fica gravado no item, pois pode diferir do preco atual da safra.
 */
public class ItemPedidoSafra {

    private Safra safra;
    private BigDecimal quantidadeSolicitada;
    private BigDecimal precoUnidadePedido;

    public ItemPedidoSafra(Safra safra, BigDecimal quantidadeSolicitada, BigDecimal precoUnidadePedido) {
        setSafra(safra);
        setQuantidadeSolicitada(quantidadeSolicitada);
        setPrecoUnidadePedido(precoUnidadePedido);
    }

    /** RN10 e RF20: subtotal = quantidade solicitada x preco do item. */
    public BigDecimal getSubtotal() {
        return quantidadeSolicitada.multiply(precoUnidadePedido).setScale(2, RoundingMode.HALF_UP);
    }

    public Safra getSafra() { return safra; }
    public void setSafra(Safra safra) { this.safra = Validacao.obrigatorio(safra, "Safra"); }

    public BigDecimal getQuantidadeSolicitada() { return quantidadeSolicitada; }
    public void setQuantidadeSolicitada(BigDecimal quantidadeSolicitada) {
        safra.validarQuantidadeSolicitada(quantidadeSolicitada);
        this.quantidadeSolicitada = quantidadeSolicitada;
    }

    public BigDecimal getPrecoUnidadePedido() { return precoUnidadePedido; }
    public void setPrecoUnidadePedido(BigDecimal precoUnidadePedido) {
        this.precoUnidadePedido = Validacao.naoNegativo(precoUnidadePedido, "Preco da unidade no pedido")
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return quantidadeSolicitada + " x " + safra.getProduto().getNome() + " a R$ " + precoUnidadePedido;
    }
}
