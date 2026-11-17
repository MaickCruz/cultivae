package br.com.cultivae.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.cultivae.enums.Perfil;

/** Tabela produtor (perfil PRODUTOR): CPF unico e descricao opcional. */
public class Produtor extends Usuario {

    private String cpf;
    private String descricao;
    private final List<Estabelecimento> estabelecimentos = new ArrayList<>();
    private final List<Pedido> pedidosRecebidos = new ArrayList<>();

    public Produtor(Integer id, String nome, String email, String senha, LocalDate dataCadastro,
                    Status status, String cpf, String descricao) {
        super(id, nome, email, senha, dataCadastro, status, Perfil.PRODUTOR);
        setCpf(cpf);
        setDescricao(descricao);
    }

    // ---------- regras de negocio ----------

    /** RF15: safras disponiveis em todos os estabelecimentos do produtor. */
    public List<Safra> listarSafrasDisponiveis(LocalDate hoje) {
        List<Safra> disponiveis = new ArrayList<>();

        for (Estabelecimento estabelecimento : estabelecimentos) {
            disponiveis.addAll(estabelecimento.listarSafrasDisponiveis(hoje));
        }

        return disponiveis;
    }

    public int contarSafras() {
        int total = 0;

        for (Estabelecimento estabelecimento : estabelecimentos) {
            total += estabelecimento.getSafras().size();
        }

        return total;
    }

    /** RF23: o produtor aceita um pedido que recebeu. */
    public void aceitarPedido(Pedido pedido, Status statusAceito) {
        validarPedidoRecebido(pedido);
        pedido.alterarStatus(statusAceito);
    }

    /** RF23: o produtor recusa um pedido que recebeu. */
    public void recusarPedido(Pedido pedido, Status statusRecusado) {
        validarPedidoRecebido(pedido);
        pedido.alterarStatus(statusRecusado);
    }

    /** RF26 (consulta 14): soma dos pedidos com status Entregue. */
    public BigDecimal calcularFaturamento() {
        BigDecimal total = BigDecimal.ZERO;
        for (Pedido pedido : pedidosRecebidos) {
            if (pedido.estaEntregue()) {
                total = total.add(pedido.getValorTotal());
            }
        }
        return total;
    }

    private void validarPedidoRecebido(Pedido pedido) {
        Validacao.obrigatorio(pedido, "Pedido");
        if (pedido.getProdutor() != this) {
            throw new IllegalArgumentException("Este pedido nao foi feito para este produtor.");
        }
    }

    void registrarEstabelecimento(Estabelecimento estabelecimento) {
        estabelecimentos.add(estabelecimento);
    }

    void registrarPedido(Pedido pedido) {
        pedidosRecebidos.add(pedido);
    }

    // ---------- getters e setters ----------

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = Validacao.digitos(cpf, "CPF", 11); }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = Validacao.textoOpcional(descricao); }

    public List<Estabelecimento> getEstabelecimentos() { return Collections.unmodifiableList(estabelecimentos); }
    public List<Pedido> getPedidosRecebidos() { return Collections.unmodifiableList(pedidosRecebidos); }
}
