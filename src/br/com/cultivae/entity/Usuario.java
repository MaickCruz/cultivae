package br.com.cultivae.entity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import br.com.cultivae.enums.Perfil;

/**
 * Tabela usuario. Classe abstrata: todo usuario é um Produtor, um Comerciante
 * ou um Administrador (especializacao representada por heranca).
 */
public abstract class Usuario {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final int TAMANHO_MINIMO_SENHA = 6;

    private Integer id;
    private String nome;
    private String email;
    private String senhaHash;
    private LocalDate dataCadastro;
    private Status status;
    private final Perfil perfil;
    private final List<String> telefones = new ArrayList<>();

    protected Usuario(Integer id, String nome, String email, String senha,
                      LocalDate dataCadastro, Status status, Perfil perfil) {
        this.id = id;
        this.perfil = Validacao.obrigatorio(perfil, "Perfil");
        setNome(nome);
        setEmail(email);
        setSenha(senha);
        setDataCadastro(dataCadastro);
        setStatus(status);
    }

    // ---------- regras de negocio ----------

    /** RF02: confere a senha informada com o hash armazenado. */
    public boolean autenticar(String senhaInformada) {
        return senhaInformada != null && senhaHash.equals(gerarHash(senhaInformada));
    }

    /** Troca de senha exigindo a senha atual. */
    public void alterarSenha(String senhaAtual, String novaSenha) {
        if (!autenticar(senhaAtual)) {
            throw new IllegalArgumentException("Senha atual incorreta.");
        }
        setSenha(novaSenha);
    }

    /** RF06 e RN05: varios telefones por usuario, sem repetir o mesmo numero. */
    public void adicionarTelefone(String telefone) {
        String valido = validarTelefone(telefone);
        if (telefones.contains(valido)) {
            throw new IllegalArgumentException("Telefone ja cadastrado para este usuario.");
        }
        telefones.add(valido);
    }

    public boolean removerTelefone(String telefone) {
        return telefone != null && telefones.remove(telefone.trim());
    }

    public List<String> getTelefones() {
        return Collections.unmodifiableList(telefones);
    }

    static String validarTelefone(String telefone) {
        String limpo = Validacao.texto(telefone, "Telefone", 20);
        int quantidadeDigitos = limpo.replaceAll("[^0-9]", "").length();
        if (quantidadeDigitos < 8 || quantidadeDigitos > 15) {
            throw new IllegalArgumentException("Telefone deve ter entre 8 e 15 digitos.");
        }
        return limpo;
    }

    private static String gerarHash(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(senha.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : bytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo de hash indisponivel.", e);
        }
    }

    // ---------- getters e setters ----------

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = Validacao.texto(nome, "Nome", 100); }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        String valido = Validacao.texto(email, "E-mail", 100).toLowerCase();
        if (!EMAIL.matcher(valido).matches()) {
            throw new IllegalArgumentException("E-mail invalido: " + email);
        }
        this.email = valido;
    }

    /** Guarda apenas o hash da senha*/
    public void setSenha(String senha) {
        if (senha == null || senha.length() < TAMANHO_MINIMO_SENHA) {
            throw new IllegalArgumentException("A senha deve ter ao menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
        this.senhaHash = gerarHash(senha);
    }

    public String getSenhaHash() { return senhaHash; }

    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = Validacao.obrigatorio(dataCadastro, "Data de cadastro");
    }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = Validacao.obrigatorio(status, "Status"); }

    /** O perfil nao muda depois que o usuario e criado. */
    public Perfil getPerfil() { return perfil; }

    @Override
    public String toString() { return perfil + ": " + nome + " <" + email + ">"; }
}
