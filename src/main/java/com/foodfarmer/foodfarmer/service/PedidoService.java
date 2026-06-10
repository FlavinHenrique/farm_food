package com.foodfarmer.foodfarmer.service;

import com.foodfarmer.foodfarmer.model.Loja;
import com.foodfarmer.foodfarmer.model.Pedido;
import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public List<Pedido> getPedidosPorLojas(List<Loja> lojas) {
        return pedidoRepository.findByLojaIn(lojas);
    }

    public List<Pedido> getPedidosPorCliente(Usuario cliente) {
        return pedidoRepository.findByClienteOrderByDataPedidoDesc(cliente);
    }

    public BigDecimal calcularFaturamentoTotal(List<Pedido> pedidos) {
        return pedidos.stream()
                .map(Pedido::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public void salvarPedido(Pedido pedido) {
        pedidoRepository.save(pedido);
    }
}
