package com.foodfarmer.foodfarmer.repository;

import com.foodfarmer.foodfarmer.model.Loja;
import com.foodfarmer.foodfarmer.model.Pedido;
import com.foodfarmer.foodfarmer.model.StatusEntrega;
import com.foodfarmer.foodfarmer.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByLoja(Loja loja);
    List<Pedido> findByLojaIn(List<Loja> lojas);
    List<Pedido> findByClienteOrderByDataPedidoDesc(Usuario cliente);
    List<Pedido> findByEntregadorOrderByPrazoEntregaAsc(Usuario entregador);
    List<Pedido> findByEntregadorAndStatusEntregaOrderByPrazoEntregaAsc(Usuario entregador, StatusEntrega statusEntrega);
    List<Pedido> findByEntregadorAndRegiaoEntregaContainingIgnoreCaseOrderByPrazoEntregaAsc(Usuario entregador, String regiaoEntrega);
    List<Pedido> findByEntregadorAndPrazoEntregaBeforeOrderByPrazoEntregaAsc(Usuario entregador, LocalDateTime prazoEntrega);
    List<Pedido> findByEntregadorIsNullAndStatusEntregaOrderByDataPedidoDesc(StatusEntrega statusEntrega);
}
