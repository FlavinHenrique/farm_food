package com.foodfarmer.foodfarmer.repository;

import com.foodfarmer.foodfarmer.model.Loja;
import com.foodfarmer.foodfarmer.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LojaRepository extends JpaRepository<Loja, Long> {
    List<Loja> findByDono(Usuario dono);
}
