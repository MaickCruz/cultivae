package br.com.cultivae.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Produtor extends Usuario {
    private String telefone;
    private String razaoSocial;
    private final List<PropriedadeRural> propriedades = new ArrayList<>();

    public Produtor(int id, String nome, String sobrenome, String cpf, String email,
                    String senhaHash, String telefone, String razaoSocial) {
        super(id, nome, sobrenome, cpf, email, senhaHash);
        setTelefone(telefone);
        setRazaoSocial(razaoSocial);
    }

    public String getTelefone() { return telefone; }
    public String getRazaoSocial() { return razaoSocial; }
    public List<PropriedadeRural> getPropriedades() { return Collections.unmodifiableList(propriedades); }
    public void setTelefone(String telefone) { this.telefone = validarTexto(telefone, "Telefone"); }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = validarTexto(razaoSocial, "Razão social"); }
    public void cadastrarPropriedade(PropriedadeRural propriedade) {
        if (propriedade == null || propriedade.getProdutor() != this) {
            throw new IllegalArgumentException("A propriedade deve pertencer a este produtor.");
        }
        if (!propriedades.contains(propriedade)) propriedades.add(propriedade);
    }
    @Override public String getPerfil() { return "PRODUTOR"; }
}
