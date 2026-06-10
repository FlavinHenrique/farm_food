package com.foodfarmer.foodfarmer.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.foodfarmer.foodfarmer.model.PapelUsuario;
import com.foodfarmer.foodfarmer.model.Pedido;
import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.service.AutenticacaoService;
import com.foodfarmer.foodfarmer.service.PedidoService;

@RestController
@RequestMapping("/api/orders")
public class PedidoCheckoutApiController {
    private final PedidoService pedidoService;
    private final AutenticacaoService autenticacaoService;

    public PedidoCheckoutApiController(PedidoService pedidoService, AutenticacaoService autenticacaoService) {
        this.pedidoService = pedidoService;
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public CheckoutResponse finalizarCheckout(@RequestBody PedidoService.CheckoutOrderRequest request) {
        Usuario usuario = autenticacaoService.getUsuarioAtual().orElse(null);
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Faça login para finalizar o pedido.");
        }
        if (usuario.getPapel() != PapelUsuario.CLIENTE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Somente clientes podem finalizar pedidos.");
        }

        List<Pedido> pedidos = pedidoService.criarPedidosCheckout(usuario, request);
        List<Long> ids = pedidos.stream().map(Pedido::getId).toList();
        return new CheckoutResponse(ids, ids.isEmpty() ? null : ids.get(0), pedidos.size());
    }

    public record CheckoutResponse(List<Long> pedidosIds, Long primeiroPedidoId, int quantidadePedidos) {}
}

