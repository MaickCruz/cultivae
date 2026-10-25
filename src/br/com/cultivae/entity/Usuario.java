package br.com.cultivae.entity;

public abstract class Usuario {
    private final int id;
    private String nome;
    private String sobrenome;
    private final String cpf;
    private String email;
    private String senhaHash;
    private boolean ativo;

    protected Usuario(int id, String nome, String sobrenome, String cpf, String email, String senhaHash) {
        this.id = id;
        this.cpf = validarTexto(cpf, "CPF");
        setNome(nome);
        setSobrenome(sobrenome);
        setEmail(email);
        setSenhaHash(senhaHash);
        this.ativo = true;
    }

    protected static String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException(campo + " obrigatório.");
        return valor.trim();
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getSobrenome() { return sobrenome; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getSenhaHash() { return senhaHash; }
    public boolean isAtivo() { return ativo; }
    public void setNome(String nome) { this.nome = validarTexto(nome, "Nome"); }
    public void setSobrenome(String sobrenome) { this.sobrenome = validarTexto(sobrenome, "Sobrenome"); }
    public void setEmail(String email) { this.email = validarTexto(email, "E-mail"); }
    public void setSenhaHash(String senhaHash) { this.senhaHash = validarTexto(senhaHash, "Hash da senha"); }
    public void desativar() { ativo = false; }
    public void ativar() { ativo = true; }
    public String getNomeCompleto() { return nome + " " + sobrenome; }
    public abstract String getPerfil(); // Polimorfismo: cada tipo define seu perfil.

    @Override
    public String toString() { return getPerfil() + ": " + getNomeCompleto(); }
}
