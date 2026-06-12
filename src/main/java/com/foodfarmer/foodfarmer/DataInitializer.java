package com.foodfarmer.foodfarmer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.foodfarmer.foodfarmer.model.Categoria;
import com.foodfarmer.foodfarmer.model.Endereco;
import com.foodfarmer.foodfarmer.model.ItemPedido;
import com.foodfarmer.foodfarmer.model.Loja;
import com.foodfarmer.foodfarmer.model.PapelUsuario;
import com.foodfarmer.foodfarmer.model.Pedido;
import com.foodfarmer.foodfarmer.model.Produto;
import com.foodfarmer.foodfarmer.model.StatusEntrega;
import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.repository.CategoriaRepository;
import com.foodfarmer.foodfarmer.repository.EnderecoRepository;
import com.foodfarmer.foodfarmer.repository.LojaRepository;
import com.foodfarmer.foodfarmer.repository.PedidoRepository;
import com.foodfarmer.foodfarmer.repository.ProdutoRepository;
import com.foodfarmer.foodfarmer.repository.UsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;
    private final LojaRepository lojaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EnderecoRepository enderecoRepository;
    private final PedidoRepository pedidoRepository;

    public DataInitializer(CategoriaRepository categoriaRepository, ProdutoRepository produtoRepository, 
                          LojaRepository lojaRepository, UsuarioRepository usuarioRepository,
                          EnderecoRepository enderecoRepository,
                          PedidoRepository pedidoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository;
        this.lojaRepository = lojaRepository;
        this.usuarioRepository = usuarioRepository;
        this.enderecoRepository = enderecoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Criação de dados apenas se o banco estiver completamente vazio
        boolean needSeedData = usuarioRepository.count() == 0;

        Usuario producer;
        Usuario customer;
        Usuario courier;
        Loja store = null;
        Categoria legumes = null;
        Categoria frutas = null;
        Produto cebola = null;
        Produto brocolis = null;
        Produto maca = null;

        if (needSeedData) {
            // Cria todos os dados do zero
            producer = usuarioRepository.save(new Usuario(
                null, 
                "Produtor Exemplo", 
                "Fazenda do Zé", 
                null, 
                "00.000.000/0001-91", 
                null, 
                null, 
                null, 
                "produtor@farmfood.com", 
                "123", 
                PapelUsuario.PRODUTOR, 
                true, 
                null
            ));
            customer = usuarioRepository.save(new Usuario(
                null, 
                "Maria Silva", 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                "cliente@farmfood.com", 
                "123", 
                PapelUsuario.CLIENTE, 
                true, 
                null
            ));
            courier = usuarioRepository.save(new Usuario(
                null, 
                "Diego Entregas", 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                "entregador@farmfood.com", 
                "123", 
                PapelUsuario.ENTREGADOR, 
                true, 
                null
            ));

            legumes = categoriaRepository.save(new Categoria(null, "Legumes", "bi-carrot"));
            frutas = categoriaRepository.save(new Categoria(null, "Frutas", "bi-apple"));

            store = lojaRepository.save(new Loja(
                null,
                "Horta do Ze",
                "Produtos frescos direto do produtor",
                "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&q=80&w=200",
                "Hortifruti",
                "Rua das Flores, 123 - São Paulo/SP",
                "(11) 99999-9999",
                "Seg-Sex: 08:00-18:00",
                true,
                true,
                producer,
                null
            ));

            cebola = produtoRepository.save(new Produto(
                null,
                "Cebola Roxa",
                "Cebola roxa fresca e selecionada",
                new BigDecimal("5.50"),
                "https://images.unsplash.com/photo-1508747703725-719777637510?auto=format&fit=crop&q=80&w=400",
                "kg",
                100,
                true,
                10,
                true,
                legumes,
                store
            ));
            brocolis = produtoRepository.save(new Produto(
                null,
                "Brocolis Organico",
                "Brocolis ninja colhido no dia",
                new BigDecimal("8.00"),
                "https://images.unsplash.com/photo-1459411621453-7b03977f4bfc?auto=format&fit=crop&q=80&w=400",
                "un",
                50,
                false,
                0,
                true,
                legumes,
                store
            ));
            maca = produtoRepository.save(new Produto(
                null,
                "Maca Fuji",
                "Fruta doce para consumo diario",
                new BigDecimal("11.90"),
                "https://images.unsplash.com/photo-1567306226416-28f0efdc88ce?auto=format&fit=crop&q=80&w=400",
                "kg",
                80,
                false,
                0,
                true,
                frutas,
                store
            ));

            enderecoRepository.save(new Endereco(
                null,
                "Casa",
                "Rua das Flores",
                "120",
                "Apto 21",
                "Centro",
                "Sao Paulo",
                "SP",
                "01001000",
                customer
            ));
        } else {
            // Recupera dados existentes
            producer = usuarioRepository.findByPapel(PapelUsuario.PRODUTOR).stream().findFirst().orElse(null);
            customer = usuarioRepository.findByPapel(PapelUsuario.CLIENTE).stream().findFirst().orElse(null);
            courier = usuarioRepository.findByPapel(PapelUsuario.ENTREGADOR).stream().findFirst().orElse(null);
        }

        // Cria pedidos de teste apenas se NÃO houver nenhum pedido e tivermos todos os dados necessários
        if (pedidoRepository.count() == 0 && producer != null && customer != null && courier != null) {
            // Tenta recuperar loja e produtos existentes se não foram criados agora
            if (store == null) {
                store = lojaRepository.findByDono(producer).stream().findFirst().orElse(null);
            }

            if (store != null) {
                if (cebola == null) {
                    cebola = produtoRepository.findByLoja(store).stream().findFirst().orElse(null);
                }

                if (cebola != null) {
                    pedidoRepository.save(criarPedidoSeed(customer, store, courier, cebola, 2, "Centro", StatusEntrega.PENDENTE, 3, null));
                }
            }
        }

        System.out.println("Aplicacao iniciada. Dados iniciais do Farm Food verificados (banco não resetado!).");
    }

    private Pedido criarPedidoSeed(Usuario customer, Loja store, Usuario courier, Produto product, int quantity,
            String region, StatusEntrega deliveryStatus, int deadlineOffsetHours, LocalDateTime deliveredAt) {
        Pedido pedido = new Pedido();
        pedido.setCliente(customer);
        pedido.setLoja(store);
        pedido.setEntregador(courier);
        pedido.setDataPedido(LocalDateTime.now().minusHours(Math.max(1, 4 - deadlineOffsetHours)));
        pedido.setPrazoEntrega(LocalDateTime.now().plusHours(deadlineOffsetHours));
        pedido.setStatusEntrega(deliveryStatus);
        pedido.setStatus(resolveOrderStatus(deliveryStatus));
        pedido.setRegiaoEntrega(region);
        pedido.setCepEntrega("01001000");
        pedido.setLogradouroEntrega("Rua das Flores");
        pedido.setNumeroEntrega("120");
        pedido.setComplementoEntrega("Apto 21");
        pedido.setBairroEntrega(region);
        pedido.setCidadeEntrega("Sao Paulo");
        pedido.setEstadoEntrega("SP");
        pedido.setValorFrete(new BigDecimal("12.90"));

        BigDecimal subtotal = product.getPrecoComDesconto().multiply(BigDecimal.valueOf(quantity));
        pedido.setValorProdutos(subtotal);
        pedido.setValorDesconto(BigDecimal.ZERO);
        pedido.setStatusPagamento(com.foodfarmer.foodfarmer.model.StatusPagamento.PAGO);
        pedido.setTipoMetodoPagamento(com.foodfarmer.foodfarmer.model.TipoMetodoPagamento.PIX);
        pedido.setValorTotal(subtotal.add(pedido.getValorFrete()));

        if (deliveryStatus == StatusEntrega.EM_ROTA || deliveryStatus == StatusEntrega.CHEGOU_DESTINO || deliveryStatus == StatusEntrega.ENTREGUE) {
            pedido.setInicioRotaEm(LocalDateTime.now().minusHours(1));
        }
        if (deliveryStatus == StatusEntrega.CHEGOU_DESTINO || deliveryStatus == StatusEntrega.ENTREGUE) {
            pedido.setChegadaDestinoEm(LocalDateTime.now().minusMinutes(25));
        }
        if (deliveryStatus == StatusEntrega.ENTREGUE) {
            pedido.setEntregueEm(deliveredAt);
            pedido.setAssinaturaEntrega("Maria Silva");
            pedido.setComprovanteEntrega("https://example.com/comprovantes/pedido-seed.jpg");
        }

        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setPedido(pedido);
        itemPedido.setProduto(product);
        itemPedido.setQuantidade(quantity);
        itemPedido.setPrecoUnitario(product.getPreco());
        pedido.setItens(List.of(itemPedido));
        return pedido;
    }

    private String resolveOrderStatus(StatusEntrega deliveryStatus) {
        return switch (deliveryStatus) {
            case PENDENTE -> "Pendente";
            case ACEITO -> "Aceito para entrega";
            case EM_ROTA -> "Em rota";
            case CHEGOU_DESTINO -> "Entregador no destino";
            case ENTREGUE -> "Finalizado";
            case PROBLEMA -> "Problema na entrega";
            case CANCELADO -> "Cancelado";
        };
    }
}
