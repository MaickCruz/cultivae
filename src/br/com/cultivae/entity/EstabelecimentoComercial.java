package br.com.cultivae.entity;

public class EstabelecimentoComercial {
    private final int id;
    private String cidade;
    private String uf;
    private String cep;
    private String endereco;
    private String telefone;
    private String email;
    private boolean ativo = true;
    private final Comerciante comerciante;

    public EstabelecimentoComercial(int id, String cidade, String uf, String cep,
                                   String endereco, String telefone, String email, Comerciante comerciante) {
        if (comerciante == null) throw new IllegalArgumentException("Comerciante obrigatório.");
        this.id = id;
        this.comerciante = comerciante;
        this.cidade = Usuario.validarTexto(cidade, "Cidade");
        this.uf = Usuario.validarTexto(uf, "UF");
        this.cep = Usuario.validarTexto(cep, "CEP");
        this.endereco = Usuario.validarTexto(endereco, "Endereço");
        atualizarContato(telefone, email);
        comerciante.cadastrarEstabelecimento(this);
    }
    public int getId() { return id; }
    public String getCidade() { return cidade; }
    public String getUf() { return uf; }
    public String getCep() { return cep; }
    public String getEndereco() { return endereco; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public boolean isAtivo() { return ativo; }
    public Comerciante getComerciante() { return comerciante; }
    public void atualizarContato(String telefone, String email) {
        this.telefone = Usuario.validarTexto(telefone, "Telefone");
        this.email = Usuario.validarTexto(email, "E-mail");
    }
    public void desativar() { ativo = false; }
}
