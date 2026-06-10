package com.foodfarmer.foodfarmer.repository;

import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.model.PapelUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByPapel(PapelUsuario papel);
}
