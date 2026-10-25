package br.com.cultivae.entity;
	
public class Produto {
    private final int id;
    private String nome;
    private String categoria;

    public Produto(int id, String nome, String categoria) {
        this.id = id;
        setNome(nome);
        setCategoria(categoria);
    }
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public void setNome(String nome) { this.nome = Usuario.validarTexto(nome, "Nome"); }
    public void setCategoria(String categoria) { this.categoria = Usuario.validarTexto(categoria, "Categoria"); }
    @Override public String toString() { return nome + " (" + categoria + ")"; }
}