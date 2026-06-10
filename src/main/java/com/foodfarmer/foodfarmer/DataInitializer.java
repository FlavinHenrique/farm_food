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
        System.out.println("Banco de dados limpo e inicializador de dados desativado.");
    }
}
