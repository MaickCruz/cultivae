package br.com.cultivae.entity;

import java.math.BigDecimal;

public class Safra {
    private final int id;
    private final Produto produto;
    private final PropriedadeRural propriedade;
    private String unidadeMedida;
    private BigDecimal quantidadeDisponivel;
    private BigDecimal precoUnitario;
    private boolean ativa = true;

    public Safra(int id, Produto produto, PropriedadeRural propriedade, String unidadeMedida,
                 BigDecimal quantidadeDisponivel, BigDecimal precoUnitario) {
        if (produto == null || propriedade == null) throw new IllegalArgumentException("Produto e propriedade obrigatórios.");
        if (!propriedade.isAtiva()) throw new IllegalStateException("Propriedade inativa.");
        this.id = id;
        this.produto = produto;
        this.propriedade = propriedade;
        this.unidadeMedida = Usuario.validarTexto(unidadeMedida, "Unidade de medida");
        definirQuantidade(quantidadeDisponivel);
        alterarPreco(precoUnitario);
        propriedade.cadastrarSafra(this);
    }
    public int getId() { return id; }
    public Produto getProduto() { return produto; }
    public PropriedadeRural getPropriedade() { return propriedade; }
    public Produtor getProdutor() { return propriedade.getProdutor(); }
    public String getUnidadeMedida() { return unidadeMedida; }
    public BigDecimal getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public boolean isAtiva() { return ativa; }
    public void alterarPreco(BigDecimal preco) {
        if (preco == null || preco.signum() <= 0) throw new IllegalArgumentException("Preço deve ser positivo.");
        this.precoUnitario = preco;
    }
    public void definirQuantidade(BigDecimal quantidade) {
        if (quantidade == null || quantidade.signum() < 0) throw new IllegalArgumentException("Estoque não pode ser negativo.");
        this.quantidadeDisponivel = quantidade;
    }
    public boolean temEstoque(BigDecimal quantidade) {
        return ativa && quantidade != null && quantidade.signum() > 0
            && quantidadeDisponivel.compareTo(quantidade) >= 0;
    }
    public void baixarEstoque(BigDecimal quantidade) {
        if (!temEstoque(quantidade)) throw new IllegalStateException("Estoque insuficiente ou safra inativa.");
        quantidadeDisponivel = quantidadeDisponivel.subtract(quantidade);
    }
    public void reporEstoque(BigDecimal quantidade) {
        if (quantidade == null || quantidade.signum() <= 0) throw new IllegalArgumentException("Quantidade inválida.");
        quantidadeDisponivel = quantidadeDisponivel.add(quantidade);
    }
    public void desativar() { ativa = false; }
}
