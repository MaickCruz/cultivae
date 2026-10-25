package br.com.cultivae.entity;

public class Administrador extends Usuario {
    private int nivelAcesso;

    public Administrador(int id, String nome, String sobrenome, String cpf, String email,
                         String senhaHash, int nivelAcesso) {
        super(id, nome, sobrenome, cpf, email, senhaHash);
        setNivelAcesso(nivelAcesso);
    }
    public int getNivelAcesso() { return nivelAcesso; }
    public void setNivelAcesso(int nivelAcesso) {
        if (nivelAcesso < 1) throw new IllegalArgumentException("Nível de acesso inválido.");
        this.nivelAcesso = nivelAcesso;
    }
    public void desativarUsuario(Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("Usuário inválido.");
        usuario.desativar();
    }
    @Override public String getPerfil() { return "ADMINISTRADOR"; }
}

