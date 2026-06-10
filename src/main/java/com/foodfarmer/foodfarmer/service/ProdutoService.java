package com.foodfarmer.foodfarmer.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.foodfarmer.foodfarmer.model.Categoria;
import com.foodfarmer.foodfarmer.model.Loja;
import com.foodfarmer.foodfarmer.model.Produto;
import com.foodfarmer.foodfarmer.repository.CategoriaRepository;
import com.foodfarmer.foodfarmer.repository.LojaRepository;
import com.foodfarmer.foodfarmer.repository.ProdutoRepository;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final LojaRepository lojaRepository;

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository, LojaRepository lojaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.lojaRepository = lojaRepository;
    }

    public List<Produto> getTodosProdutos() {
        return produtoRepository.findAll();
    }

    public List<Produto> getProdutosEmPromocao() {
        return produtoRepository.findByEmPromocaoTrue();
    }

    public List<Produto> getProdutosPorCategoria(Long categoriaId) {
        if (categoriaId == null) {
            return getTodosProdutos();
        }
        return produtoRepository.findByCategoriaId(categoriaId);
    }

    public List<Categoria> getTodasCategorias() {
        return categoriaRepository.findAll();
    }

    public List<Loja> getTodasLojas() {
        return lojaRepository.findAll();
    }

    public Optional<Produto> getProdutoPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return produtoRepository.findById(id);
    }

    public List<Produto> getProdutosMesmoNome(String nome) {
        if (nome == null) {
            return List.of();
        }
        return produtoRepository.findByNome(nome);
    }

    public List<Produto> getProdutosPorLoja(Long lojaId) {
        if (lojaId == null) {
            return List.of();
        }
        return produtoRepository.findByLojaId(lojaId);
    }

    public void excluirProduto(Long id) {
        if (id != null) {
            produtoRepository.deleteById(id);
        }
    }

    public Categoria salvarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula");
        }
        return categoriaRepository.save(categoria);
    }

    public Produto salvarProduto(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo");
        }
        return produtoRepository.save(produto);
    }
}
