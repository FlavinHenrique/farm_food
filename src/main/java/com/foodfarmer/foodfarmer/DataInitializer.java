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
        if (usuarioRepository.count() == 0) {
            Usuario producer = usuarioRepository.save(new Usuario(null, "Produtor Exemplo", "produtor@farmfood.com", "123", "00.000.000/0001-91", PapelUsuario.PRODUTOR, null));
            Usuario customer = usuarioRepository.save(new Usuario(null, "Maria Silva", "cliente@farmfood.com", "123", null, PapelUsuario.CLIENTE, null));
            Usuario courier = usuarioRepository.save(new Usuario(null, "Diego Entregas", "entregador@farmfood.com", "123", null, PapelUsuario.ENTREGADOR, null));

            Categoria legumes = categoriaRepository.save(new Categoria(null, "Legumes", "bi-carrot"));
            Categoria frutas = categoriaRepository.save(new Categoria(null, "Frutas", "bi-apple"));

            Loja store = lojaRepository.save(new Loja(
                null,
                "Horta do Ze",
                "Produtos frescos direto do produtor",
                "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&q=80&w=200",
                producer,
                null
            ));

            Produto cebola = produtoRepository.save(new Produto(
                null,
                "Cebola Roxa",
                "Cebola roxa fresca e selecionada",
                new BigDecimal("5.50"),
                "https://images.unsplash.com/photo-1508747703725-719777637510?auto=format&fit=crop&q=80&w=400",
                "kg",
                true,
                10,
                legumes,
                store
            ));
            Produto brocolis = produtoRepository.save(new Produto(
                null,
                "Brocolis Organico",
                "Brocolis ninja colhido no dia",
                new BigDecimal("8.00"),
                "https://images.unsplash.com/photo-1459411621453-7b03977f4bfc?auto=format&fit=crop&q=80&w=400",
                "un",
                false,
                0,
                legumes,
                store
            ));
            Produto maca = produtoRepository.save(new Produto(
                null,
                "Maca Fuji",
                "Fruta doce para consumo diario",
                new BigDecimal("11.90"),
                "https://images.unsplash.com/photo-1567306226416-28f0efdc88ce?auto=format&fit=crop&q=80&w=400",
                "kg",
                false,
                0,
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

            if (pedidoRepository.count() == 0) {
                pedidoRepository.save(criarPedidoSeed(customer, store, courier, cebola, 2, "Centro", StatusEntrega.PENDENTE, 3, null));
                pedidoRepository.save(criarPedidoSeed(customer, store, courier, brocolis, 3, "Zona Sul", StatusEntrega.EM_ROTA, 1, null));
                pedidoRepository.save(criarPedidoSeed(customer, store, courier, maca, 1, "Moema", StatusEntrega.ENTREGUE, -5, LocalDateTime.now().minusHours(2)));
            }
        }

        System.out.println("Aplicacao iniciada. Dados iniciais do Farm Food verificados.");
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

        BigDecimal subtotal = product.getPreco().multiply(BigDecimal.valueOf(quantity));
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
