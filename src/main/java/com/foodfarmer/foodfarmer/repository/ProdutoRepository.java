package com.foodfarmer.foodfarmer.repository;

import com.foodfarmer.foodfarmer.model.Produto;
import com.foodfarmer.foodfarmer.model.Loja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByEmPromocaoTrueAndAtivoTrue();
    List<Produto> findByCategoriaIdAndAtivoTrue(Long categoriaId);
    List<Produto> findByNomeAndAtivoTrue(String nome);
    List<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);
    List<Produto> findByLojaIdAndAtivoTrue(Long lojaId);
    List<Produto> findByLojaAndAtivoTrue(Loja loja);
    List<Produto> findByLojaId(Long lojaId);
    List<Produto> findByLoja(Loja loja);
    List<Produto> findByAtivoTrue();
}
