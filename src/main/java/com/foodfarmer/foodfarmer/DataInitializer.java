package com.foodfarmer.foodfarmer;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.foodfarmer.foodfarmer.model.Categoria;
import com.foodfarmer.foodfarmer.model.ItemPedido;
import com.foodfarmer.foodfarmer.model.Loja;
import com.foodfarmer.foodfarmer.model.PapelUsuario;
import com.foodfarmer.foodfarmer.model.Pedido;
import com.foodfarmer.foodfarmer.model.Produto;
import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.repository.CategoriaRepository;
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
    private final PedidoRepository pedidoRepository;

    public DataInitializer(CategoriaRepository categoriaRepository, ProdutoRepository produtoRepository, 
                          LojaRepository lojaRepository, UsuarioRepository usuarioRepository,
                          PedidoRepository pedidoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository;
        this.lojaRepository = lojaRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Inicializador de dados mock para testes (descomente as seções abaixo para repopular o banco se estiver vazio)
        
        /*
        if (usuarioRepository.count() == 0) {
            Usuario producer = new Usuario(null, "Produtor Exemplo", "produtor@exemplo.com", "123", "00.000.000/0001-91", PapelUsuario.PRODUTOR, null);
            Usuario customer = new Usuario(null, "Maria Silva", "maria@exemplo.com", "123", null, PapelUsuario.CLIENTE, null);
            usuarioRepository.saveAll(List.of(producer, customer));

            Categoria legumes = new Categoria(null, "Legumes", "bi-carrot");
            Categoria frutas = new Categoria(null, "Frutas", "bi-apple");
            categoriaRepository.saveAll(List.of(legumes, frutas));

            Loja store1 = new Loja(null, "Horta do Zé", "Produtos frescos direto do produtor", 
                "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&q=80&w=200", producer, null);
            lojaRepository.save(store1);

            Produto p1 = new Produto(null, "Cebola", "Cebola roxa fresca", new BigDecimal("5.50"), 
                "https://images.unsplash.com/photo-1508747703725-719777637510?auto=format&fit=crop&q=80&w=400", "kg", true, 10, legumes, store1);
            Produto p2 = new Produto(null, "Brócolis", "Brócolis ninja orgânico", new BigDecimal("8.00"), 
                "https://images.unsplash.com/photo-1459411621453-7b03977f4bfc?auto=format&fit=crop&q=80&w=400", "un", false, 0, legumes, store1);
            produtoRepository.saveAll(List.of(p1, p2));
        }
        */

        System.out.println("Aplicação iniciada. Verificação de dados mock concluída.");
    }
}
