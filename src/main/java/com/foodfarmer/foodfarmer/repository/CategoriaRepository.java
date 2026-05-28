package com.foodfarmer.foodfarmer.repository;

import com.foodfarmer.foodfarmer.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
