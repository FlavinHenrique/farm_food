package com.foodfarmer.foodfarmer.controller;

import com.foodfarmer.foodfarmer.dto.ItemPedidoDTO;
import com.foodfarmer.foodfarmer.dto.PedidoDTO;
import com.foodfarmer.foodfarmer.model.*;
import com.foodfarmer.foodfarmer.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class PedidoController {

    private final PedidoService pedidoService;
    private final ProdutoService produtoService;
    private final AutenticacaoService autenticacaoService;

    public PedidoController(PedidoService pedidoService, ProdutoService produtoService, AutenticacaoService autenticacaoService) {
        this.pedidoService = pedidoService;
        this.produtoService = produtoService;
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody PedidoDTO pedidoDTO) {
        if (!autenticacaoService.estaLogado()) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }

        Usuario cliente = autenticacaoService.getUsuarioAtual().get();
        List<ItemPedidoDTO> itensDTO = pedidoDTO.getItens();

        if (itensDTO == null || itensDTO.isEmpty()) {
            return ResponseEntity.badRequest().body("O pedido não possui itens");
        }

        // Convertendo PedidoDTO para CheckoutOrderRequest
        List<PedidoService.CheckoutItemRequest> itens = itensDTO.stream()
                .map(item -> new PedidoService.CheckoutItemRequest(item.getProdutoId(), item.getQuantidade()))
                .toList();

        PedidoService.CheckoutOrderRequest request = new PedidoService.CheckoutOrderRequest(
                cliente.getNome(),
                cliente.getEmail(),
                null,
                null,
                null,
                false,
                null,
                pedidoDTO.getCepEntrega(),
                pedidoDTO.getLogradouroEntrega(),
                pedidoDTO.getNumeroEntrega(),
                pedidoDTO.getComplementoEntrega(),
                pedidoDTO.getBairroEntrega(),
                pedidoDTO.getCidadeEntrega(),
                pedidoDTO.getEstadoEntrega(),
                "PIX",
                pedidoDTO.getValorTotal(),
                null,
                itens
        );

        try {
            List<Pedido> pedidos = pedidoService.criarPedidosCheckout(cliente, request);
            return ResponseEntity.ok(Map.of(
                "message", "Pedido(s) criado(s) com sucesso",
                "orderIds", pedidos.stream().map(Pedido::getId).collect(Collectors.toList())
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
