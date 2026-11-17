package br.com.cultivae.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.cultivae.enums.Perfil;

/** Tabela comerciante (perfil COMERCIANTE): CNPJ unico e nome fantasia obrigatorio. */
public class Comerciante extends Usuario {

    private String cnpj;
    private String nomeFantasia;
    private final List<Estabelecimento> estabelecimentos = new ArrayList<>();
    private final List<Pedido> pedidosRealizados = new ArrayList<>();

    public Comerciante(Integer id, String nome, String email, String senha, LocalDate dataCadastro,
                       Status status, String cnpj, String nomeFantasia) {
        super(id, nome, email, senha, dataCadastro, status, Perfil.COMERCIANTE);
        setCnpj(cnpj);
        setNomeFantasia(nomeFantasia);
    }

    // ---------- regras de negocio ----------

    /** abre um pedido para um produtor (os itens sao adicionados depois). */
    public Pedido realizarPedido(Produtor produtor, Status statusInicial, String observacao) {
        return new Pedido(null, this, produtor, statusInicial, observacao);
    }

    /** total de pedidos do comerciante, de qualquer status. */
    public int contarPedidos() {
        return pedidosRealizados.size();
    }

    void registrarEstabelecimento(Estabelecimento estabelecimento) {
        estabelecimentos.add(estabelecimento);
    }

    void registrarPedido(Pedido pedido) {
        pedidosRealizados.add(pedido);
    }

    // ---------- getters e setters ----------

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = Validacao.digitos(cnpj, "CNPJ", 14); }

    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = Validacao.texto(nomeFantasia, "Nome fantasia", 100);
    }

    public List<Estabelecimento> getEstabelecimentos() { return Collections.unmodifiableList(estabelecimentos); }
    public List<Pedido> getPedidosRealizados() { return Collections.unmodifiableList(pedidosRealizados); }
}
