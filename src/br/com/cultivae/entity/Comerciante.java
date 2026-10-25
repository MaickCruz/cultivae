package br.com.cultivae.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Comerciante extends Usuario {
    private String telefone;
    private String razaoSocial;
    private final List<EstabelecimentoComercial> estabelecimentos = new ArrayList<>();

    public Comerciante(int id, String nome, String sobrenome, String cpf, String email,
                       String senhaHash, String telefone, String razaoSocial) {
        super(id, nome, sobrenome, cpf, email, senhaHash);
        setTelefone(telefone);
        setRazaoSocial(razaoSocial);
    }
    public String getTelefone() { return telefone; }
    public String getRazaoSocial() { return razaoSocial; }
    public List<EstabelecimentoComercial> getEstabelecimentos() { return Collections.unmodifiableList(estabelecimentos); }
    public void setTelefone(String telefone) { this.telefone = validarTexto(telefone, "Telefone"); }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = validarTexto(razaoSocial, "Razão social"); }
    public void cadastrarEstabelecimento(EstabelecimentoComercial estabelecimento) {
        if (estabelecimento == null || estabelecimento.getComerciante() != this) {
            throw new IllegalArgumentException("O estabelecimento deve pertencer a este comerciante.");
        }
        if (!estabelecimentos.contains(estabelecimento)) estabelecimentos.add(estabelecimento);
    }
    @Override public String getPerfil() { return "COMERCIANTE"; }
}
