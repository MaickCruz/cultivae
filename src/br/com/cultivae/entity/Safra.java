package br.com.cultivae.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Tabela safra: oferta de um produto feita por um estabelecimento.
 * Quantidade e preco podem ser nulos (como no banco), mas nunca negativos.
 */
public class Safra {

    private Integer id;
    private Produto produto;
    private Estabelecimento estabelecimento;
    private Integer quantidadeDisponivel;
    private BigDecimal precoUnitario;
    private LocalDate dataColheita;
    private LocalDate dataDisponibilidade;
    private LocalDate dataValidade;
    private Status status;

    public Safra(Integer id, Produto produto, Estabelecimento estabelecimento, Integer quantidadeDisponivel,
                 BigDecimal precoUnitario, LocalDate dataColheita, LocalDate dataDisponibilidade,
                 LocalDate dataValidade, Status status) {
        this.id = id;
        setProduto(produto);
        setEstabelecimento(estabelecimento);
        setQuantidadeDisponivel(quantidadeDisponivel);
        setPrecoUnitario(precoUnitario);
        definirDatas(dataColheita, dataDisponibilidade, dataValidade);
        setStatus(status);
        estabelecimento.registrarSafra(this);
    }

    // ---------- regras de negocio ----------

    /** RF15: status Disponível, estoque positivo e hoje dentro do periodo de disponibilidade e validade. */
    public boolean estaDisponivel(LocalDate hoje) {
        return status.possuiNome(Status.NOME_DISPONIVEL)
                && quantidadeDisponivel != null && quantidadeDisponivel > 0
                && !hoje.isBefore(dataDisponibilidade)
                && !hoje.isAfter(dataValidade);
    }

    /** RF17 (consulta 15): safra com validade ultrapassada. */
    public boolean estaVencida(LocalDate hoje) {
        return hoje.isAfter(dataValidade);
    }

    /** RN11: a safra so pode ser vendida pelo produtor dono do estabelecimento. */
    public boolean pertenceAoProdutor(Produtor produtor) {
        return estabelecimento.pertenceAoProdutor(produtor);
    }

    /** RF24: verifica se ha estoque para a quantidade pedida. */
    public boolean temEstoque(BigDecimal quantidade) {
        return quantidadeDisponivel != null
                && BigDecimal.valueOf(quantidadeDisponivel).compareTo(quantidade) >= 0;
    }

    /** O estoque e inteiro, entao so quantidades positivas e inteiras podem ser pedidas. */
    public void validarQuantidadeSolicitada(BigDecimal quantidade) {
        Validacao.obrigatorio(quantidade, "Quantidade solicitada");
        if (quantidade.signum() <= 0) {
            throw new IllegalArgumentException("Quantidade solicitada deve ser maior que zero.");
        }
        if (quantidade.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("A quantidade deve ser um numero inteiro, pois o estoque e inteiro.");
        }
    }

    /** RN12: baixa de estoque sem permitir saldo negativo. */
    public void baixarEstoque(BigDecimal quantidade) {
        validarQuantidadeSolicitada(quantidade);
        if (!temEstoque(quantidade)) {
            throw new IllegalStateException("Estoque insuficiente para a safra de " + produto.getNome() + ".");
        }
        quantidadeDisponivel -= quantidade.intValueExact();
    }

    public void reporEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade a repor deve ser maior que zero.");
        }
        quantidadeDisponivel = (quantidadeDisponivel == null ? 0 : quantidadeDisponivel) + quantidade;
    }

    /** Altera as tres datas juntas, respeitando colheita <= disponibilidade <= validade (RN07). */
    public void definirDatas(LocalDate colheita, LocalDate disponibilidade, LocalDate validade) {
        validarDatas(colheita, disponibilidade, validade);
        this.dataColheita = colheita;
        this.dataDisponibilidade = disponibilidade;
        this.dataValidade = validade;
    }

    private static void validarDatas(LocalDate colheita, LocalDate disponibilidade, LocalDate validade) {
        Validacao.obrigatorio(colheita, "Data de colheita");
        Validacao.obrigatorio(disponibilidade, "Data de disponibilidade");
        Validacao.obrigatorio(validade, "Data de validade");
        if (colheita.isAfter(disponibilidade) || disponibilidade.isAfter(validade)) {
            throw new IllegalArgumentException(
                    "As datas devem seguir a ordem: colheita <= disponibilidade <= validade.");
        }
    }

    // ---------- getters e setters ----------

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = Validacao.obrigatorio(produto, "Produto"); }

    public Estabelecimento getEstabelecimento() { return estabelecimento; }
    public void setEstabelecimento(Estabelecimento estabelecimento) {
        this.estabelecimento = Validacao.obrigatorio(estabelecimento, "Estabelecimento");
    }

    public Integer getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public void setQuantidadeDisponivel(Integer quantidadeDisponivel) {
        if (quantidadeDisponivel != null && quantidadeDisponivel < 0) {
            throw new IllegalArgumentException("Quantidade disponivel nao pode ser negativa.");
        }
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario == null
                ? null
                : Validacao.naoNegativo(precoUnitario, "Preco unitario").setScale(2, RoundingMode.HALF_UP);
    }

    public LocalDate getDataColheita() { return dataColheita; }
    public void setDataColheita(LocalDate dataColheita) {
        validarDatas(dataColheita, dataDisponibilidade, dataValidade);
        this.dataColheita = dataColheita;
    }

    public LocalDate getDataDisponibilidade() { return dataDisponibilidade; }
    public void setDataDisponibilidade(LocalDate dataDisponibilidade) {
        validarDatas(dataColheita, dataDisponibilidade, dataValidade);
        this.dataDisponibilidade = dataDisponibilidade;
    }

    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) {
        validarDatas(dataColheita, dataDisponibilidade, dataValidade);
        this.dataValidade = dataValidade;
    }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = Validacao.obrigatorio(status, "Status"); }

    @Override
    public String toString() {
        return "Safra de " + produto.getNome() + " (" + estabelecimento.getNome() + ")";
    }
}