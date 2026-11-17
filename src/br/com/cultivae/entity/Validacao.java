package br.com.cultivae.entity;

import java.math.BigDecimal;

/** Metodos utilitarios de validacao usados pelas entidades. */
final class Validacao {

    private Validacao() {
    }

    static String texto(String valor, String campo, int tamanhoMaximo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " e obrigatorio.");
        }
        String limpo = valor.trim();
        if (limpo.length() > tamanhoMaximo) {
            throw new IllegalArgumentException(campo + " excede " + tamanhoMaximo + " caracteres.");
        }
        return limpo;
    }

    static String textoOpcional(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }

    static <T> T obrigatorio(T valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException(campo + " e obrigatorio.");
        }
        return valor;
    }

    /** Remove caracteres nao numericos e exige o tamanho informado (CPF, CNPJ, CEP). */
    static String digitos(String valor, String campo, int tamanho) {
        String numeros = texto(valor, campo, 50).replaceAll("[^0-9]", "");
        if (numeros.length() != tamanho) {
            throw new IllegalArgumentException(campo + " deve ter " + tamanho + " digitos.");
        }
        return numeros;
    }

    static BigDecimal naoNegativo(BigDecimal valor, String campo) {
        obrigatorio(valor, campo);
        if (valor.signum() < 0) {
            throw new IllegalArgumentException(campo + " nao pode ser negativo.");
        }
        return valor;
    }
}

