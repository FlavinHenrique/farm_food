package com.foodfarmer.foodfarmer.repository;

import com.foodfarmer.foodfarmer.model.Loja;
import com.foodfarmer.foodfarmer.model.Pedido;
import com.foodfarmer.foodfarmer.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByLoja(Loja loja);
    List<Pedido> findByLojaIn(List<Loja> lojas);
    List<Pedido> findByClienteOrderByDataPedidoDesc(Usuario cliente);
}
