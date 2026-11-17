package br.com.cultivae.entity;

public class TipoEstabelecimento {

    private Integer id;
    private String tipo;

    public TipoEstabelecimento(Integer id, String tipo) {
        this.id = id;
        setTipo(tipo);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = Validacao.texto(tipo, "Tipo de estabelecimento", 100); }

    @Override
    public String toString() { return tipo; }
}
