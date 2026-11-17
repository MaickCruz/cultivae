package br.com.cultivae.entity;

/** Tabela categoria: classifica os produtos. */
public class Categoria {

    private Integer id;
    private String nome;

    public Categoria(Integer id, String nome) {
        this.id = id;
        setNome(nome);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = Validacao.texto(nome, "Nome da categoria", 100); }

    @Override
    public String toString() { return nome; }
}
