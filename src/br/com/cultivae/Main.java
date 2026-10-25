package br.com.cultivae;

import java.math.BigDecimal;

import br.com.cultivae.entity.Comerciante;
import br.com.cultivae.entity.EstabelecimentoComercial;
import br.com.cultivae.entity.ItemPedido;
import br.com.cultivae.entity.Pedido;
import br.com.cultivae.entity.Produto;
import br.com.cultivae.entity.Produtor;
import br.com.cultivae.entity.PropriedadeRural;
import br.com.cultivae.entity.Safra;

public class Main {
    public static void main(String[] args) {
        Produtor produtor = new Produtor(1, "Ana", "Silva", "11111111111",
            "ana@exemplo.com", "hash-ficticio", "15999999999", "Horta da Ana");
        Comerciante comerciante = new Comerciante(2, "Bruno", "Souza", "22222222222",
            "bruno@exemplo.com", "hash-ficticio", "15888888888", "Mercado do Bruno");

        PropriedadeRural propriedade = new PropriedadeRural(1, "Sítio Verde", "Votorantim",
            "SP", "18110000", "Estrada Rural, 100", "15999999999", "sitio@exemplo.com", produtor);
        EstabelecimentoComercial mercado = new EstabelecimentoComercial(1, "Sorocaba", "SP",
            "18000000", "Rua Central, 50", "15888888888", "mercado@exemplo.com", comerciante);

        Produto tomate = new Produto(1, "Tomate", "Hortaliças");
        Safra safra = new Safra(1, tomate, propriedade, "kg",
            new BigDecimal("100.00"), new BigDecimal("5.50"));

        Pedido pedido = new Pedido(1, produtor, mercado);
        pedido.adicionarItem(new ItemPedido(1, safra, new BigDecimal("2.50")));
        System.out.println("Perfis: " + produtor.getPerfil() + " / " + comerciante.getPerfil());
        System.out.println("Antes: " + pedido + " | Estoque: " + safra.getQuantidadeDisponivel());
        pedido.aceitar();
        System.out.println("Depois do aceite: " + pedido + " | Estoque: " + safra.getQuantidadeDisponivel());
        pedido.iniciarTransporte();
        pedido.concluir();
        System.out.println("Final: " + pedido);
    }
}
