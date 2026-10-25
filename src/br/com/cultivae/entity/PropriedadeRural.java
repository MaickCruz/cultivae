package br.com.cultivae.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PropriedadeRural {
    private final int id;
    private String nome;
    private String cidade;
    private String uf;
    private String cep;
    private String endereco;
    private String telefone;
    private String email;
    private boolean ativa = true;
    private final Produtor produtor;
    private final List<Safra> safras = new ArrayList<>();

    public PropriedadeRural(int id, String nome, String cidade, String uf, String cep,
                           String endereco, String telefone, String email, Produtor produtor) {
        if (produtor == null) throw new IllegalArgumentException("Produtor obrigatório.");
        this.id = id;
        this.produtor = produtor;
        this.nome = Usuario.validarTexto(nome, "Nome");
        this.cidade = Usuario.validarTexto(cidade, "Cidade");
        this.uf = Usuario.validarTexto(uf, "UF");
        this.cep = Usuario.validarTexto(cep, "CEP");
        this.endereco = Usuario.validarTexto(endereco, "Endereço");
        this.telefone = Usuario.validarTexto(telefone, "Telefone");
        this.email = Usuario.validarTexto(email, "E-mail");
        produtor.cadastrarPropriedade(this);
    }
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCidade() { return cidade; }
    public String getUf() { return uf; }
    public String getCep() { return cep; }
    public String getEndereco() { return endereco; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public boolean isAtiva() { return ativa; }
    public Produtor getProdutor() { return produtor; }
    public List<Safra> getSafras() { return Collections.unmodifiableList(safras); }
    public void atualizarContato(String telefone, String email) {
        this.telefone = Usuario.validarTexto(telefone, "Telefone");
        this.email = Usuario.validarTexto(email, "E-mail");
    }
    public void desativar() { ativa = false; }
    public void cadastrarSafra(Safra safra) {
        if (safra == null || safra.getPropriedade() != this) throw new IllegalArgumentException("Safra não pertence à propriedade.");
        if (!safras.contains(safra)) safras.add(safra);
    }
}
