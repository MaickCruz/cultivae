package br.com.cultivae.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pedido {
    private final int id;
    private final Produtor produtor;
    private final Comerciante comerciante;
    private final EstabelecimentoComercial estabelecimento;
    private final LocalDateTime dataPedido;
    private final List<ItemPedido> itens = new ArrayList<>();
    private StatusPedido status = StatusPedido.PENDENTE;

    public Pedido(int id, Produtor produtor, EstabelecimentoComercial estabelecimento) {
        if (produtor == null || estabelecimento == null) throw new IllegalArgumentException("Partes obrigatórias.");
        if (!produtor.isAtivo() || !estabelecimento.isAtivo() || !estabelecimento.getComerciante().isAtivo())
            throw new IllegalStateException("Participante inativo.");
        this.id = id;
        this.produtor = produtor;
        this.estabelecimento = estabelecimento;
        this.comerciante = estabelecimento.getComerciante();
        this.dataPedido = LocalDateTime.now();
    }
    public int getId() { return id; }
    public Produtor getProdutor() { return produtor; }
    public Comerciante getComerciante() { return comerciante; }
    public EstabelecimentoComercial getEstabelecimento() { return estabelecimento; }
    public LocalDateTime getDataPedido() { return dataPedido; }
    public StatusPedido getStatus() { return status; }
    public List<ItemPedido> getItens() { return Collections.unmodifiableList(itens); }

    public void adicionarItem(ItemPedido item) {
        exigirStatus(StatusPedido.PENDENTE);
        if (item == null || item.getSafra().getProdutor() != produtor)
            throw new IllegalArgumentException("Todos os itens devem ser do mesmo produtor do pedido.");
        for (ItemPedido existente : itens) {
            if (existente.getSafra() == item.getSafra())
                throw new IllegalArgumentException("Safra já adicionada ao pedido; reúna a quantidade num único item.");
        }
        itens.add(item);
    }
    public BigDecimal calcularValorTotal() {
        return itens.stream().map(ItemPedido::calcularSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public boolean validarEstoque() {
        return !itens.isEmpty() && itens.stream().allMatch(i -> i.getSafra().temEstoque(i.getQuantidade()));
    }
    // Escolha didática do fluxo: o estoque é baixado quando o produtor ACEITA o pedido.
    public void aceitar() {
        exigirStatus(StatusPedido.PENDENTE);
        if (!validarEstoque()) throw new IllegalStateException("Pedido vazio ou sem estoque disponível.");
        for (ItemPedido item : itens) item.getSafra().baixarEstoque(item.getQuantidade());
        status = StatusPedido.ACEITO;
    }
    public void recusar() {
        exigirStatus(StatusPedido.PENDENTE);
        status = StatusPedido.RECUSADO;
    }
    public void iniciarTransporte() {
        exigirStatus(StatusPedido.ACEITO);
        status = StatusPedido.EM_TRANSPORTE;
    }
    public void concluir() {
        exigirStatus(StatusPedido.EM_TRANSPORTE);
        status = StatusPedido.CONCLUIDO;
    }
    public void cancelar() {
        if (status == StatusPedido.ACEITO) {
            for (ItemPedido item : itens) item.getSafra().reporEstoque(item.getQuantidade());
        } else if (status != StatusPedido.PENDENTE) {
            throw new IllegalStateException("Cancelamento permitido apenas para pedido pendente ou aceito.");
        }
        status = StatusPedido.CANCELADO;
    }
    private void exigirStatus(StatusPedido esperado) {
        if (status != esperado) throw new IllegalStateException("Operação exige status " + esperado + "; atual: " + status);
    }
    @Override public String toString() {
        return "Pedido #" + id + " | " + status + " | Total: R$ " + calcularValorTotal();
    }
}
