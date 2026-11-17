package br.com.cultivae.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Tabela pedido: feito por um comerciante e recebido por um produtor. */
public class Pedido {

    private Integer id;
    private final Comerciante comerciante;
    private final Produtor produtor;
    private Status status;
    private String observacao;
    private LocalDateTime dataPedido;
    private final List<ItemPedidoSafra> itens = new ArrayList<>();

    public Pedido(Integer id, Comerciante comerciante, Produtor produtor, Status status, String observacao) {
        this.id = id;
        this.comerciante = Validacao.obrigatorio(comerciante, "Comerciante");
        this.produtor = Validacao.obrigatorio(produtor, "Produtor");
        setStatus(status);
        setObservacao(observacao);
        this.dataPedido = LocalDateTime.now();
        comerciante.registrarPedido(this);
        produtor.registrarPedido(this);
    }

    // ---------- regras de negocio ----------

    /** RF19, RN09 e RN11: adiciona uma safra ao pedido com o preco praticado. */
    public ItemPedidoSafra adicionarItem(Safra safra, BigDecimal quantidade, BigDecimal precoUnidade) {
        Validacao.obrigatorio(safra, "Safra");
        if (!safra.pertenceAoProdutor(produtor)) {
            throw new IllegalArgumentException("A safra nao pertence a um estabelecimento do produtor deste pedido.");
        }
        if (buscarItem(safra) != null) {
            throw new IllegalArgumentException("Esta safra ja esta no pedido.");
        }
        ItemPedidoSafra item = new ItemPedidoSafra(safra, quantidade, precoUnidade);
        itens.add(item);
        return item;
    }

    /** Variacao que usa o preco atual da safra. */
    public ItemPedidoSafra adicionarItem(Safra safra, BigDecimal quantidade) {
        Validacao.obrigatorio(safra, "Safra");
        if (safra.getPrecoUnitario() == null) {
            throw new IllegalArgumentException("A safra nao possui preco definido.");
        }
        return adicionarItem(safra, quantidade, safra.getPrecoUnitario());
    }

    public boolean removerItem(Safra safra) {
        ItemPedidoSafra item = buscarItem(safra);
        return item != null && itens.remove(item);
    }

    private ItemPedidoSafra buscarItem(Safra safra) {
        for (ItemPedidoSafra item : itens) {
            if (item.getSafra() == safra) {
                return item;
            }
        }
        return null;
    }

    /** RN12: o valor total e sempre a soma dos subtotais dos itens. */
    public BigDecimal calcularValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedidoSafra item : itens) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    /** RF24: confere o estoque de todos os itens e so entao da baixa, de uma vez. */
    public void confirmar(Status statusConfirmado) {
        Validacao.obrigatorio(statusConfirmado, "Status");
        if (itens.isEmpty()) {
            throw new IllegalStateException("Nao e possivel confirmar um pedido sem itens.");
        }
        for (ItemPedidoSafra item : itens) {
            if (!item.getSafra().temEstoque(item.getQuantidadeSolicitada())) {
                throw new IllegalStateException(
                        "Estoque insuficiente para a safra de " + item.getSafra().getProduto().getNome() + ".");
            }
        }
        for (ItemPedidoSafra item : itens) {
            item.getSafra().baixarEstoque(item.getQuantidadeSolicitada());
        }
        this.status = statusConfirmado;
    }

    /** RF22: registra um novo status (as transicoes permitidas ficam para a aplicacao futura). */
    public void alterarStatus(Status novoStatus) {
        setStatus(novoStatus);
    }

    public boolean estaEntregue() {
        return status.possuiNome(Status.NOME_ENTREGUE);
    }

    // ---------- getters e setters ----------

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Comerciante getComerciante() { return comerciante; }
    public Produtor getProdutor() { return produtor; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = Validacao.obrigatorio(status, "Status"); }

    /** Calculado a partir dos itens, nao pode ser informado manualmente. */
    public BigDecimal getValorTotal() { return calcularValorTotal(); }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = Validacao.textoOpcional(observacao); }

    public LocalDateTime getDataPedido() { return dataPedido; }
    public void setDataPedido(LocalDateTime dataPedido) {
        this.dataPedido = Validacao.obrigatorio(dataPedido, "Data do pedido");
    }

    public List<ItemPedidoSafra> getItens() { return Collections.unmodifiableList(itens); }

    @Override
    public String toString() {
        return "Pedido " + (id == null ? "(novo)" : "#" + id) + " de " + comerciante.getNome()
                + " para " + produtor.getNome() + " [" + status + "] R$ " + getValorTotal();
    }
}