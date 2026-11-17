package br.com.cultivae.entity;

/** Tabela status: compartilhada por usuarios, safras e pedidos. */
public class Status {

    public static final String NOME_DISPONIVEL = "Disponível";
    public static final String NOME_ENTREGUE = "Entregue";

    private Integer id;
    private String nome;

    public Status(Integer id, String nome) {
        this.id = id;
        setNome(nome);
    }

    public boolean possuiNome(String outroNome) {
        return nome.equalsIgnoreCase(outroNome);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = Validacao.texto(nome, "Nome do status", 100); }

    @Override
    public String toString() { return nome; }
}