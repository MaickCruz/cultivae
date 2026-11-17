package br.com.cultivae.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tabela estabelecimento. Pertence a um produtor OU a um comerciante, nunca aos dois
 * (CHECK ck_estab_dono). Por isso o proprietario so e definido nos construtores.
 */
public class Estabelecimento {

    private Integer id;
    private String nome;
    private TipoEstabelecimento tipo;
    private String descricao;
    private Endereco endereco;
    private final Produtor produtor;
    private final Comerciante comerciante;
    private final List<String> telefones = new ArrayList<>();
    private final List<Safra> safras = new ArrayList<>();

    /** Estabelecimento de um produtor. */
    public Estabelecimento(Integer id, String nome, TipoEstabelecimento tipo, String descricao,
                           Endereco endereco, Produtor produtor) {
        this(id, nome, tipo, descricao, endereco, Validacao.obrigatorio(produtor, "Produtor"), null);
        produtor.registrarEstabelecimento(this);
    }

    /** Estabelecimento de um comerciante. */
    public Estabelecimento(Integer id, String nome, TipoEstabelecimento tipo, String descricao,
                           Endereco endereco, Comerciante comerciante) {
        this(id, nome, tipo, descricao, endereco, null, Validacao.obrigatorio(comerciante, "Comerciante"));
        comerciante.registrarEstabelecimento(this);
    }

    private Estabelecimento(Integer id, String nome, TipoEstabelecimento tipo, String descricao,
                            Endereco endereco, Produtor produtor, Comerciante comerciante) {
        this.id = id;
        setNome(nome);
        setTipo(tipo);
        setDescricao(descricao);
        setEndereco(endereco);
        this.produtor = produtor;
        this.comerciante = comerciante;
    }

    // ---------- regras de negocio ----------

    public void adicionarTelefone(String telefone) {
        String valido = Usuario.validarTelefone(telefone);
        if (telefones.contains(valido)) {
            throw new IllegalArgumentException("Telefone ja cadastrado para este estabelecimento.");
        }
        telefones.add(valido);
    }

    public boolean removerTelefone(String telefone) {
        return telefone != null && telefones.remove(telefone.trim());
    }

    public List<Safra> listarSafrasDisponiveis(LocalDate hoje) {
        List<Safra> disponiveis = new ArrayList<>();
        for (Safra safra : safras) {
            if (safra.estaDisponivel(hoje)) {
                disponiveis.add(safra);
            }
        }
        return disponiveis;
    }

    public boolean estaNaCidade(String cidade, String estado) {
        return endereco.estaNaCidade(cidade, estado);
    }

    public boolean pertenceAoProdutor(Produtor outroProdutor) {
        return produtor != null && produtor == outroProdutor;
    }

    public Usuario getProprietario() {
        return produtor != null ? produtor : comerciante;
    }

    void registrarSafra(Safra safra) {
        safras.add(safra);
    }

    // ---------- getters e setters ----------

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = Validacao.texto(nome, "Nome do estabelecimento", 100); }

    public TipoEstabelecimento getTipo() { return tipo; }
    public void setTipo(TipoEstabelecimento tipo) { this.tipo = Validacao.obrigatorio(tipo, "Tipo"); }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = Validacao.textoOpcional(descricao); }

    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = Validacao.obrigatorio(endereco, "Endereco"); }

    /** Nulo quando o estabelecimento pertence a um comerciante. */
    public Produtor getProdutor() { return produtor; }

    /** Nulo quando o estabelecimento pertence a um produtor. */
    public Comerciante getComerciante() { return comerciante; }

    public List<String> getTelefones() { return Collections.unmodifiableList(telefones); }
    public List<Safra> getSafras() { return Collections.unmodifiableList(safras); }

    @Override
    public String toString() { return nome + " (" + tipo + ")"; }
}

