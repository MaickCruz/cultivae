package br.com.cultivae.entity;

import java.math.BigDecimal;

public class ItemPedido {
    private final int id;
    private final Safra safra;
    private final BigDecimal quantidade;
    private final BigDecimal precoPraticado; // Fotografia do preço no momento do pedido.

    public ItemPedido(int id, Safra safra, BigDecimal quantidade) {
        if (safra == null) throw new IllegalArgumentException("Safra obrigatória.");
        if (quantidade == null || quantidade.signum() <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva.");
        this.id = id;
        this.safra = safra;
        this.quantidade = quantidade;
        this.precoPraticado = safra.getPrecoUnitario();
    }
    public int getId() { return id; }
    public Safra getSafra() { return safra; }
    public BigDecimal getQuantidade() { return quantidade; }
    public BigDecimal getPrecoPraticado() { return precoPraticado; }
    public BigDecimal calcularSubtotal() { return precoPraticado.multiply(quantidade); }
}
