package br.com.cultivae.entity;

import java.time.LocalDate;

import br.com.cultivae.enums.Perfil;

/** Tabela administrador (perfil ADMINISTRADOR): possui um nivel de acesso. */
public class Administrador extends Usuario {

    private String nivelAcesso;

    public Administrador(Integer id, String nome, String email, String senha, LocalDate dataCadastro,
                         Status status, String nivelAcesso) {
        super(id, nome, email, senha, dataCadastro, status, Perfil.ADMINISTRADOR);
        setNivelAcesso(nivelAcesso);
    }

    // ---------- regras de negocio ----------

    public boolean possuiNivelAcesso(String nivel) {
        return nivelAcesso.equalsIgnoreCase(nivel);
    }

    public void alterarStatusUsuario(Usuario usuario, Status novoStatus) {
        Validacao.obrigatorio(usuario, "Usuario").setStatus(novoStatus);
    }

    public void alterarStatusSafra(Safra safra, Status novoStatus) {
        Validacao.obrigatorio(safra, "Safra").setStatus(novoStatus);
    }

    // ---------- getters e setters ----------

    public String getNivelAcesso() { return nivelAcesso; }
    public void setNivelAcesso(String nivelAcesso) {
        this.nivelAcesso = Validacao.texto(nivelAcesso, "Nivel de acesso", 100);
    }
}
