package com.foodfarmer.foodfarmer.repository;

import com.foodfarmer.foodfarmer.model.MetodoPagamento;
import com.foodfarmer.foodfarmer.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MetodoPagamentoRepository extends JpaRepository<MetodoPagamento, Long> {
    List<MetodoPagamento> findByUsuario(Usuario usuario);
}
