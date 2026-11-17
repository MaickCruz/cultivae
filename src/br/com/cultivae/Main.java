package br.com.cultivae;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.cultivae.entity.Administrador;
import br.com.cultivae.entity.Categoria;
import br.com.cultivae.entity.Comerciante;
import br.com.cultivae.entity.Endereco;
import br.com.cultivae.entity.Estabelecimento;
import br.com.cultivae.entity.Pedido;
import br.com.cultivae.entity.Produto;
import br.com.cultivae.entity.Produtor;
import br.com.cultivae.entity.Safra;
import br.com.cultivae.entity.Status;
import br.com.cultivae.entity.TipoEstabelecimento;
import br.com.cultivae.enums.Perfil;

/** Testes simples das regras de negocio do Cultivae (sem banco, sem framework). */
public class Main {

    private static int aprovados = 0;
    private static int reprovados = 0;

    public static void main(String[] args) {
        LocalDate hoje = LocalDate.of(2026, 11, 17);

        // ---------- dados base ----------
        Status ativo = new Status(1, "Ativo");
        Status disponivel = new Status(2, "Disponível");
        Status pendente = new Status(3, "Pendente");
        Status aceito = new Status(4, "Aceito");
        Status recusado = new Status(5, "Recusado");
        Status entregue = new Status(6, "Entregue");
        Status esgotado = new Status(7, "Esgotado");

        Categoria hortalicas = new Categoria(1, "Hortaliças");
        Produto alface = new Produto(1, "Alface", "Alface crespa", "unidade", hortalicas);
        Produto tomate = new Produto(2, "Tomate", null, "kg", hortalicas);

        TipoEstabelecimento sitio = new TipoEstabelecimento(1, "Sítio");
        TipoEstabelecimento mercado = new TipoEstabelecimento(2, "Mercado");

        // ---------- usuarios ----------
        titulo("Usuarios");
        Produtor joao = new Produtor(1, "João Silva", "joao@cultivae.com", "senha123", hoje, ativo,
                "123.456.789-01", "Produtor de hortaliças");
        Comerciante mercadoBom = new Comerciante(2, "Maria Souza", "maria@mercado.com", "abc12345", hoje, ativo,
                "12.345.678/0001-90", "Mercado Bom Preço");
        Produtor outroProdutor = new Produtor(4, "Pedro Lima", "pedro@cultivae.com", "pedro123", hoje, ativo,
                "98765432100", null);
        Administrador admin = new Administrador(3, "Ana Admin", "ana@cultivae.com", "admin123", hoje, ativo, "TOTAL");

        verificar("Perfil do produtor e PRODUTOR", joao.getPerfil() == Perfil.PRODUTOR);
        verificar("CPF guardado apenas com digitos", joao.getCpf().equals("12345678901"));
        verificar("CNPJ guardado apenas com digitos", mercadoBom.getCnpj().equals("12345678000190"));
        verificar("Senha nao fica em texto puro", !joao.getSenhaHash().equals("senha123"));
        verificar("Autenticacao com senha correta", joao.autenticar("senha123"));
        verificar("Autenticacao com senha errada", !joao.autenticar("errada"));
        joao.alterarSenha("senha123", "novaSenha1");
        verificar("Senha alterada com sucesso", joao.autenticar("novaSenha1"));
        esperarErro("E-mail invalido e rejeitado", () -> joao.setEmail("sem-arroba"));
        esperarErro("Senha curta e rejeitada", () -> joao.setSenha("123"));
        esperarErro("CPF com tamanho errado e rejeitado", () -> joao.setCpf("123"));
        esperarErro("Nome fantasia vazio e rejeitado", () -> mercadoBom.setNomeFantasia("  "));

        joao.adicionarTelefone("(15) 99999-0001");
        joao.adicionarTelefone("(15) 3333-0002");
        verificar("Usuario com 2 telefones", joao.getTelefones().size() == 2);
        esperarErro("Telefone repetido e rejeitado", () -> joao.adicionarTelefone("(15) 99999-0001"));

        admin.alterarStatusUsuario(outroProdutor, esgotado);
        verificar("Administrador altera status de usuario", outroProdutor.getStatus() == esgotado);
        outroProdutor.setStatus(ativo);

        // ---------- estabelecimentos ----------
        titulo("Estabelecimentos");
        Endereco endSitio = new Endereco(1, "Estrada Rural", "Zona Rural", "Votorantim", "sp", "18110-000", "S/N", null);
        Endereco endMercado = new Endereco(2, "Rua das Flores", "Centro", "Sorocaba", "SP", "18000000", "100", "Loja 2");
        Endereco endOutro = new Endereco(3, "Estrada Velha", "Zona Rural", "Votorantim", "SP", "18110001", "50", null);

        Estabelecimento sitioJoao = new Estabelecimento(1, "Sítio Boa Colheita", sitio, "Hortaliças frescas", endSitio, joao);
        Estabelecimento lojaMercado = new Estabelecimento(2, "Mercado Bom Preço", mercado, null, endMercado, mercadoBom);
        Estabelecimento sitioPedro = new Estabelecimento(3, "Sítio do Pedro", sitio, null, endOutro, outroProdutor);

        verificar("UF convertida para maiuscula", endSitio.getEstado().equals("SP"));
        verificar("Estabelecimento de produtor nao tem comerciante", sitioJoao.getComerciante() == null);
        verificar("Estabelecimento de comerciante nao tem produtor", lojaMercado.getProdutor() == null);
        verificar("Proprietario do sitio e o Joao", sitioJoao.getProprietario() == joao);
        verificar("Busca regional encontra Votorantim/SP", sitioJoao.estaNaCidade("votorantim", "SP"));
        verificar("Busca regional nao encontra Sorocaba", !sitioJoao.estaNaCidade("Sorocaba", "SP"));
        verificar("Produtor enxerga seu estabelecimento", joao.getEstabelecimentos().size() == 1);
        sitioJoao.adicionarTelefone("(15) 3333-1111");
        esperarErro("Telefone repetido no estabelecimento", () -> sitioJoao.adicionarTelefone("(15) 3333-1111"));
        esperarErro("CEP invalido e rejeitado", () -> endSitio.setCep("123"));
        verificar("Enderecos iguais pelo CEP, logradouro e numero",
                endSitio.mesmoLocal(new Endereco(9, "estrada rural", "X", "Y", "SP", "18110000", "s/n", null)));

        // ---------- safras ----------
        titulo("Safras");
        Safra safraAlface = new Safra(1, alface, sitioJoao, 100, new BigDecimal("2.50"),
                hoje.minusDays(5), hoje.minusDays(1), hoje.plusDays(10), disponivel);
        Safra safraTomate = new Safra(2, tomate, sitioJoao, 50, new BigDecimal("6.00"),
                hoje.minusDays(3), hoje, hoje.plusDays(7), disponivel);
        Safra safraVencida = new Safra(3, alface, sitioJoao, 20, new BigDecimal("2.00"),
                hoje.minusDays(40), hoje.minusDays(35), hoje.minusDays(5), disponivel);
        Safra safraPedro = new Safra(4, tomate, sitioPedro, 30, new BigDecimal("5.50"),
                hoje.minusDays(2), hoje, hoje.plusDays(5), disponivel);

        verificar("Safra de alface esta disponivel", safraAlface.estaDisponivel(hoje));
        verificar("Safra vencida nao esta disponivel", !safraVencida.estaDisponivel(hoje));
        verificar("Safra vencida e identificada como vencida", safraVencida.estaVencida(hoje));
        verificar("Produtor tem 2 safras disponiveis", joao.listarSafrasDisponiveis(hoje).size() == 2);
        verificar("Produtor tem 3 safras no total", joao.contarSafras() == 3);
        verificar("Safra pertence ao produtor dono", safraAlface.pertenceAoProdutor(joao));
        verificar("Safra nao pertence a outro produtor", !safraAlface.pertenceAoProdutor(outroProdutor));
        esperarErro("Quantidade negativa e rejeitada", () -> safraAlface.setQuantidadeDisponivel(-1));
        esperarErro("Preco negativo e rejeitado", () -> safraAlface.setPrecoUnitario(new BigDecimal("-1")));
        esperarErro("Datas fora de ordem sao rejeitadas", () -> safraAlface.setDataValidade(hoje.minusDays(30)));
        esperarErro("Estoque nao pode ficar negativo", () -> safraAlface.baixarEstoque(new BigDecimal("101")));
        safraTomate.setStatus(esgotado);
        verificar("Safra com status diferente de Disponível nao esta disponivel", !safraTomate.estaDisponivel(hoje));
        admin.alterarStatusSafra(safraTomate, disponivel);
        verificar("Administrador reativa a safra", safraTomate.estaDisponivel(hoje));

        // ---------- pedidos ----------
        titulo("Pedidos");
        Pedido pedido = mercadoBom.realizarPedido(joao, pendente, "Entregar pela manhã");
        pedido.adicionarItem(safraAlface, new BigDecimal("10"));
        pedido.adicionarItem(safraTomate, new BigDecimal("4"), new BigDecimal("5.50"));

        verificar("Subtotal do item de alface (10 x 2,50)",
                pedido.getItens().get(0).getSubtotal().compareTo(new BigDecimal("25.00")) == 0);
        verificar("Subtotal do item de tomate (4 x 5,50)",
                pedido.getItens().get(1).getSubtotal().compareTo(new BigDecimal("22.00")) == 0);
        verificar("Valor total e a soma dos subtotais (47,00)",
                pedido.getValorTotal().compareTo(new BigDecimal("47.00")) == 0);
        esperarErro("Mesma safra duas vezes no pedido", () -> pedido.adicionarItem(safraAlface, BigDecimal.ONE));
        esperarErro("Safra de outro produtor no pedido", () -> pedido.adicionarItem(safraPedro, BigDecimal.ONE));
        esperarErro("Quantidade zero e rejeitada", () -> pedido.adicionarItem(safraVencida, BigDecimal.ZERO));
        esperarErro("Quantidade fracionada e rejeitada", () -> pedido.adicionarItem(safraVencida, new BigDecimal("1.5")));
        verificar("Comerciante conta 1 pedido", mercadoBom.contarPedidos() == 1);
        verificar("Produtor recebeu 1 pedido", joao.getPedidosRecebidos().size() == 1);

        esperarErro("Outro produtor nao pode aceitar o pedido", () -> outroProdutor.aceitarPedido(pedido, aceito));
        joao.aceitarPedido(pedido, aceito);
        verificar("Produtor aceita o pedido", pedido.getStatus() == aceito);

        pedido.confirmar(aceito);
        verificar("Estoque de alface baixou de 100 para 90", safraAlface.getQuantidadeDisponivel() == 90);
        verificar("Estoque de tomate baixou de 50 para 46", safraTomate.getQuantidadeDisponivel() == 46);

        Pedido pedidoGrande = mercadoBom.realizarPedido(joao, pendente, null);
        pedidoGrande.adicionarItem(safraAlface, new BigDecimal("500"));
        esperarErro("Confirmar pedido sem estoque suficiente", () -> pedidoGrande.confirmar(aceito));
        verificar("Estoque intacto apos falha na confirmacao", safraAlface.getQuantidadeDisponivel() == 90);
        joao.recusarPedido(pedidoGrande, recusado);
        verificar("Produtor recusa o pedido", pedidoGrande.getStatus() == recusado);

        Pedido pedidoVazio = mercadoBom.realizarPedido(joao, pendente, null);
        esperarErro("Confirmar pedido sem itens", () -> pedidoVazio.confirmar(aceito));

        verificar("Faturamento so conta pedidos entregues (0 ainda)",
                joao.calcularFaturamento().compareTo(BigDecimal.ZERO) == 0);
        pedido.alterarStatus(entregue);
        verificar("Faturamento apos entrega (47,00)",
                joao.calcularFaturamento().compareTo(new BigDecimal("47.00")) == 0);
        verificar("Comerciante conta pedidos de todos os status (3)", mercadoBom.contarPedidos() == 3);

        System.out.println("\n" + pedido);
        pedido.getItens().forEach(item -> System.out.println("  " + item + " = R$ " + item.getSubtotal()));

        System.out.println("\n==== Resultado: " + aprovados + " aprovados, " + reprovados + " reprovados ====");
    }

    private static void titulo(String texto) {
        System.out.println("\n--- " + texto + " ---");
    }

    private static void verificar(String descricao, boolean condicao) {
        if (condicao) {
            aprovados++;
            System.out.println("[OK]    " + descricao);
        } else {
            reprovados++;
            System.out.println("[FALHA] " + descricao);
        }
    }

    /** Passa quando a acao lanca uma excecao de regra de negocio. */
    private static void esperarErro(String descricao, Runnable acao) {
        try {
            acao.run();
            reprovados++;
            System.out.println("[FALHA] " + descricao + " (nenhuma excecao lancada)");
        } catch (IllegalArgumentException | IllegalStateException e) {
            aprovados++;
            System.out.println("[OK]    " + descricao + " -> " + e.getMessage());
        }
    }
}