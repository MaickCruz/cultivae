package br.com.cultivae.entity;

/** Tabela produto: tipo de mercadoria (a oferta concreta e a Safra). */
public class Produto {

    private Integer id;
    private String nome;
    private String descricao;
    private String unidadeMedida;
    private Categoria categoria;

    public Produto(Integer id, String nome, String descricao, String unidadeMedida, Categoria categoria) {
        this.id = id;
        setNome(nome);
        setDescricao(descricao);
        setUnidadeMedida(unidadeMedida);
        setCategoria(categoria);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = Validacao.texto(nome, "Nome do produto", 100); }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = Validacao.textoOpcional(descricao); }

    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = Validacao.texto(unidadeMedida, "Unidade de medida", 20);
    }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) {
        this.categoria = Validacao.obrigatorio(categoria, "Categoria");
    }

    @Override
    public String toString() { return nome + " (" + unidadeMedida + ")"; }
}