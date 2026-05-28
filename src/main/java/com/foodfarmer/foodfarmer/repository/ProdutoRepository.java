package com.foodfarmer.foodfarmer.repository;

import com.foodfarmer.foodfarmer.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByEmPromocaoTrue();
    List<Produto> findByCategoriaId(Long categoriaId);
}
