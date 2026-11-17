package br.com.cultivae;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

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

/**
 * Simula uma venda com interacao pelo console.
 * Categoria e produtos sao criados previamente no codigo;
 * o usuario escolhe as safras e informa as quantidades.
 */
public class TesteVendaConsole {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LocalDate hoje = LocalDate.now();

        // Status
        Status ativo = new Status(1, "Ativo");
        Status disponivel = new Status(2, "Disponível");
        Status pendente = new Status(3, "Pendente");
        Status aceito = new Status(4, "Aceito");
        Status recusado = new Status(5, "Recusado");
        Status entregue = new Status(6, "Entregue");

        // Produtos criados previamente: nao sao solicitados pelo console
        Categoria categoria = new Categoria(1, "Hortaliças");
        Produto alface = new Produto(1, "Alface", "Alface crespa", "unidade", categoria);
        Produto tomate = new Produto(2, "Tomate", "Tomate italiano", "kg", categoria);
        Produto cenoura = new Produto(3, "Cenoura", "Cenoura fresca", "kg", categoria);

        // Usuarios e estabelecimento
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

        // Safras disponiveis para o comerciante escolher
        Safra safraAlface = new Safra(
                1, alface, sitio, 100, new BigDecimal("2.50"),
                hoje.minusDays(1), hoje, hoje.plusDays(15), disponivel
        );

        Safra safraTomate = new Safra(
                2, tomate, sitio, 50, new BigDecimal("6.00"),
                hoje.minusDays(1), hoje, hoje.plusDays(15), disponivel
        );

        Safra safraCenoura = new Safra(
                3, cenoura, sitio, 30, new BigDecimal("4.00"),
                hoje.minusDays(1), hoje, hoje.plusDays(15), disponivel
        );

        List<Safra> catalogo = List.of(safraAlface, safraTomate, safraCenoura);

        System.out.println("=== NOVO PEDIDO - CULTIVAE ===");
        System.out.println("Comerciante: " + comerciante.getNomeFantasia());
        System.out.println("Produtor: " + produtor.getNome());
        System.out.print("Observacao para o produtor (opcional): ");
        String observacao = scanner.nextLine();

        Pedido pedido = comerciante.realizarPedido(produtor, pendente, observacao);

        boolean continuar = true;

        while (continuar) {
            System.out.println("\n--- SAFRAS DISPONIVEIS ---");

            for (int i = 0; i < catalogo.size(); i++) {
                Safra safra = catalogo.get(i);
                System.out.printf(
                        "%d - %s | R$ %s por %s | Estoque: %d%n",
                        i + 1,
                        safra.getProduto().getNome(),
                        safra.getPrecoUnitario(),
                        safra.getProduto().getUnidadeMedida(),
                        safra.getQuantidadeDisponivel()
                );
            }

            System.out.println("0 - Finalizar escolha de itens");
            int opcao = lerInteiro(scanner, "Escolha um produto: ");

            if (opcao == 0) {
                continuar = false;
                continue;
            }

            if (opcao < 1 || opcao > catalogo.size()) {
                System.out.println("Opcao invalida.");
                continue;
            }

            Safra safraEscolhida = catalogo.get(opcao - 1);
            int quantidade = lerInteiro(scanner, "Quantidade desejada: ");

            try {
                pedido.adicionarItem(safraEscolhida, BigDecimal.valueOf(quantidade));
                System.out.println("Item adicionado ao pedido.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Nao foi possivel adicionar: " + e.getMessage());
            }
        }

        if (pedido.getItens().isEmpty()) {
            System.out.println("\nNenhum item foi adicionado. Pedido nao pode ser confirmado.");
            scanner.close();
            return;
        }

        System.out.println("\n--- RESUMO DO PEDIDO ---");
        for (ItemPedidoSafra item : pedido.getItens()) {
            System.out.println(
                    item.getQuantidadeSolicitada() + " x "
                    + item.getSafra().getProduto().getNome()
                    + " = R$ " + item.getSubtotal()
            );
        }

        System.out.println("Total: R$ " + pedido.getValorTotal());

        System.out.print("\nO produtor aceita o pedido? (S/N): ");
        String resposta = scanner.nextLine().trim();

        if (!resposta.equalsIgnoreCase("S")) {
            produtor.recusarPedido(pedido, recusado);
            System.out.println("Pedido recusado pelo produtor.");
            scanner.close();
            return;
        }

        try {
            produtor.aceitarPedido(pedido, aceito);
            pedido.confirmar(aceito);

            System.out.println("\nPedido aceito e confirmado!");
            System.out.println("--- ESTOQUE APOS A VENDA ---");

            for (Safra safra : catalogo) {
                System.out.println(
                        safra.getProduto().getNome() + ": "
                        + safra.getQuantidadeDisponivel()
                );
            }

            System.out.print("\nA entrega foi realizada? (S/N): ");
            resposta = scanner.nextLine().trim();

            if (resposta.equalsIgnoreCase("S")) {
                pedido.alterarStatus(entregue);
                System.out.println("Pedido marcado como entregue.");
            } else {
                System.out.println("Pedido permanece com status: " + pedido.getStatus());
            }

            System.out.println("Faturamento do produtor: R$ " + produtor.calcularFaturamento());

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Nao foi possivel confirmar o pedido: " + e.getMessage());
        }

        System.out.println("\nStatus final do pedido: " + pedido.getStatus());
        System.out.println("=== FIM DA SIMULACAO ===");
        scanner.close();
    }

    private static int lerInteiro(Scanner scanner, String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String entrada = scanner.nextLine().trim();

            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Digite um numero inteiro valido.");
            }
        }
    }
}
