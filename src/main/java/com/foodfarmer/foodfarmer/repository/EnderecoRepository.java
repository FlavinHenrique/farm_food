package com.foodfarmer.foodfarmer.repository;

import com.foodfarmer.foodfarmer.model.Endereco;
import com.foodfarmer.foodfarmer.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    List<Endereco> findByUsuario(Usuario usuario);
}
