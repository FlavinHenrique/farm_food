package com.foodfarmer.foodfarmer.repository;

import com.foodfarmer.foodfarmer.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    Optional<Transacao> findByPedidoId(Long pedidoId);
    List<Transacao> findByPedidoLojaId(Long lojaId);
}
