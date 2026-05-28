package com.foodfarmer.foodfarmer;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.foodfarmer.foodfarmer.model.Categoria;
import com.foodfarmer.foodfarmer.model.Loja;
import com.foodfarmer.foodfarmer.model.PapelUsuario;
import com.foodfarmer.foodfarmer.model.Produto;
import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.repository.CategoriaRepository;
import com.foodfarmer.foodfarmer.repository.LojaRepository;
import com.foodfarmer.foodfarmer.repository.ProdutoRepository;
import com.foodfarmer.foodfarmer.repository.UsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;
    private final LojaRepository lojaRepository;
    private final UsuarioRepository usuarioRepository;

    public DataInitializer(CategoriaRepository categoriaRepository, ProdutoRepository produtoRepository, 
                          LojaRepository lojaRepository, UsuarioRepository usuarioRepository) {
        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository;
        this.lojaRepository = lojaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() > 0) {
            return; // Dados já inicializados
        }

        // Usuarios (Criando usuários mock para simulação)
        Usuario producer = new Usuario(null, "Produtor Exemplo", "produtor@exemplo.com", "123", "00.000.000/0001-91", PapelUsuario.PRODUTOR, null);
        Usuario customer = new Usuario(null, "Maria Silva", "maria@exemplo.com", "123", null, PapelUsuario.CLIENTE, null);
        usuarioRepository.saveAll(List.of(producer, customer));

        // Categorias
        Categoria legumes = new Categoria(null, "Legumes", "bi-carrot");
        Categoria frutas = new Categoria(null, "Frutas", "bi-apple");
        Categoria graos = new Categoria(null, "Grãos", "bi-box-seam");
        Categoria padaria = new Categoria(null, "Padaria", "bi-egg-fried");
        categoriaRepository.saveAll(List.of(legumes, frutas, graos, padaria));

        // Lojas
        Loja store1 = new Loja(null, "Horta do Zé", "Produtos frescos direto do produtor", 
            "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&q=80&w=200", producer, null);
        Loja store2 = new Loja(null, "Vegan Delight", "Tudo o que você precisa para sua dieta vegana", 
            "https://images.unsplash.com/photo-1473448912268-2022ce9509d8?auto=format&fit=crop&q=80&w=200", producer, null);
        lojaRepository.saveAll(List.of(store1, store2));

        // Produtos
        Produto p1 = new Produto(null, "Cebola", "Cebola roxa fresca", new BigDecimal("5.50"), 
            "https://images.unsplash.com/photo-1508747703725-719777637510?auto=format&fit=crop&q=80&w=400", "kg", true, 10, legumes, store1);
        Produto p2 = new Produto(null, "Brócolis", "Brócolis ninja orgânico", new BigDecimal("8.00"), 
            "https://images.unsplash.com/photo-1459411621453-7b03977f4bfc?auto=format&fit=crop&q=80&w=400", "un", false, 0, legumes, store1);
        Produto p3 = new Produto(null, "Banana Nanica", "Banana nanica madura", new BigDecimal("4.50"), 
            "https://images.unsplash.com/photo-1571771894821-ad99026a0947?auto=format&fit=crop&q=80&w=400", "kg", true, 15, frutas, store1);
        Produto p4 = new Produto(null, "Cenoura", "Cenoura fresca", new BigDecimal("3.20"), 
            "https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?auto=format&fit=crop&q=80&w=400", "kg", false, 0, legumes, store1);
        Produto p5 = new Produto(null, "Melancia", "Melancia doce", new BigDecimal("15.00"), 
            "https://images.unsplash.com/photo-1587049633562-ad3002f02551?auto=format&fit=crop&q=80&w=400", "un", false, 0, frutas, store2);
        Produto p6 = new Produto(null, "Abacaxi", "Abacaxi pérola", new BigDecimal("7.00"), 
            "https://images.unsplash.com/photo-1550258114-b834e70e9be1?auto=format&fit=crop&q=80&w=400", "un", true, 20, frutas, store2);

        produtoRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6));
    }
}
