package br.com.cultivae.entity;

/** Tabela endereco. Combinacao CEP + logradouro + numero e unica no banco. */
public class Endereco {

    private Integer id;
    private String logradouro;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private String numero;
    private String complemento;

    public Endereco(Integer id, String logradouro, String bairro, String cidade, String estado,
                    String cep, String numero, String complemento) {
        this.id = id;
        setLogradouro(logradouro);
        setBairro(bairro);
        setCidade(cidade);
        setEstado(estado);
        setCep(cep);
        setNumero(numero);
        setComplemento(complemento);
    }

    /** Regra de unicidade do banco: mesmo CEP, logradouro e numero representam o mesmo local. */
    public boolean mesmoLocal(Endereco outro) {
        return outro != null
                && cep.equals(outro.cep)
                && logradouro.equalsIgnoreCase(outro.logradouro)
                && numero.equalsIgnoreCase(outro.numero);
    }

    /** Apoia a busca regional (RF16). */
    public boolean estaNaCidade(String cidadeBuscada, String estadoBuscado) {
        return cidade.equalsIgnoreCase(cidadeBuscada.trim())
                && estado.equalsIgnoreCase(estadoBuscado.trim());
    }

    public String getEnderecoFormatado() {
        String texto = logradouro + ", " + numero;
        if (complemento != null) {
            texto += " (" + complemento + ")";
        }
        return texto + " - " + bairro + ", " + cidade + "/" + estado + " - CEP " + cep;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = Validacao.texto(logradouro, "Logradouro", 100); }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = Validacao.texto(bairro, "Bairro", 100); }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = Validacao.texto(cidade, "Cidade", 100); }

    public String getEstado() { return estado; }
    public void setEstado(String estado) {
        String uf = Validacao.texto(estado, "Estado", 2).toUpperCase();
        if (uf.length() != 2) {
            throw new IllegalArgumentException("Estado deve ter 2 letras (UF).");
        }
        this.estado = uf;
    }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = Validacao.digitos(cep, "CEP", 8); }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = Validacao.texto(numero, "Numero", 100); }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = Validacao.textoOpcional(complemento); }

    @Override
    public String toString() { return getEnderecoFormatado(); }
}