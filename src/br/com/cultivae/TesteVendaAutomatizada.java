package br.com.cultivae;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.cultivae.entity.Categoria;
import br.com.cultivae.entity.Comerciante;
import br.com.cultivae.entity.Endereco;
import br.com.cultivae.entity.Estabelecimento;
import br.com.cultivae.entity.ItemPedidoSafra;
import br.com.cultivae.entity.Pedido;
import br.com.cultivae.entity.Produto;
import br.com.cultivae.entity.Produtor;
import br.com.cultivae.entity.Safra;
import br.com.cultivae.entity.Status;
import br.com.cultivae.entity.TipoEstabelecimento;

public class TesteVendaAutomatizada {

    public static void main(String[] args) {
        LocalDate hoje = LocalDate.now();

        Status ativo = new Status(1, "Ativo");
        Status disponivel = new Status(2, "Disponível");
        Status pendente = new Status(3, "Pendente");
        Status aceito = new Status(4, "Aceito");
        Status entregue = new Status(6, "Entregue");

        Categoria categoria = new Categoria(1, "Hortaliças");
        Produto alface = new Produto(1, "Alface", "Alface crespa", "unidade", categoria);
        Produto tomate = new Produto(2, "Tomate", "Tomate italiano", "kg", categoria);

        Produtor produtor = new Produtor(
                1, "João Silva", "joao@cultivae.com", "senha123",
                hoje, ativo, "12345678901", "Produtor de hortaliças"
        );

        Comerciante comerciante = new Comerciante(
                2, "Maria Souza", "maria@mercado.com", "senha123",
                hoje, ativo, "12345678000190", "Mercado Bom Preço"
        );

        Endereco endereco = new Endereco(
                1, "Estrada Rural", "Zona Rural", "Votorantim", "SP",
                "18110000", "S/N", null
        );

        TipoEstabelecimento tipoSitio = new TipoEstabelecimento(1, "Sítio");

        Estabelecimento sitio = new Estabelecimento(
                1, "Sítio Boa Colheita", tipoSitio,
                "Produção de hortaliças", endereco, produtor
        );

        Safra safraAlface = new Safra(
                1, alface, sitio, 100, new BigDecimal("2.50"),
                hoje.minusDays(1), hoje, hoje.plusDays(15), disponivel
        );

        Safra safraTomate = new Safra(
                2, tomate, sitio, 50, new BigDecimal("6.00"),
                hoje.minusDays(1), hoje, hoje.plusDays(15), disponivel
        );

        Pedido pedido = comerciante.realizarPedido(
                produtor, pendente, "Entrega no período da manhã"
        );

        pedido.adicionarItem(safraAlface, new BigDecimal("10"));
        pedido.adicionarItem(safraTomate, new BigDecimal("4"));

        System.out.println("=== SIMULAÇÃO AUTOMÁTICA DE VENDA ===");
        System.out.println("Comerciante: " + comerciante.getNomeFantasia());
        System.out.println("Produtor: " + produtor.getNome());
        System.out.println("\nItens do pedido:");

        for (ItemPedidoSafra item : pedido.getItens()) {
            System.out.println(
                    "- " + item.getQuantidadeSolicitada() + " x "
                    + item.getSafra().getProduto().getNome()
                    + " | Subtotal: R$ " + item.getSubtotal()
            );
        }

        System.out.println("\nTotal do pedido: R$ " + pedido.getValorTotal());

        produtor.aceitarPedido(pedido, aceito);
        pedido.confirmar(aceito);

        System.out.println("\nPedido aceito e confirmado.");
        System.out.println("Estoque de alface: " + safraAlface.getQuantidadeDisponivel());
        System.out.println("Estoque de tomate: " + safraTomate.getQuantidadeDisponivel());

        pedido.alterarStatus(entregue);

        System.out.println("\nStatus final: " + pedido.getStatus());
        System.out.println("Faturamento do produtor: R$ " + produtor.calcularFaturamento());
        System.out.println("=== FIM DA SIMULAÇÃO ===");
    }
}
