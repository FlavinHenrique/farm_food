package com.foodfarmer.foodfarmer.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.foodfarmer.foodfarmer.model.PapelUsuario;
import com.foodfarmer.foodfarmer.model.Pedido;
import com.foodfarmer.foodfarmer.model.StatusEntrega;
import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.service.AutenticacaoService;
import com.foodfarmer.foodfarmer.service.EntregadorNotificationService;
import com.foodfarmer.foodfarmer.service.PedidoService;

@RestController
@RequestMapping("/delivery/api")
public class EntregadorApiController {
    private final AutenticacaoService autenticacaoService;
    private final PedidoService pedidoService;
    private final EntregadorNotificationService notificationService;

    public EntregadorApiController(AutenticacaoService autenticacaoService, PedidoService pedidoService,
            EntregadorNotificationService notificationService) {
        this.autenticacaoService = autenticacaoService;
        this.pedidoService = pedidoService;
        this.notificationService = notificationService;
    }

    @GetMapping("/pedidos")
    public List<DeliveryCardResponse> listarPedidos(
        @RequestParam(defaultValue = "false") boolean historico
    ) {
        Usuario entregador = requireEntregador();
        return pedidoService.listarPedidosEntregador(entregador, null, null, null, historico).stream()
            .map(this::toResponse)
            .toList();
    }

    @PostMapping("/pedidos/{pedidoId}/status")
    @ResponseStatus(HttpStatus.OK)
    public DeliveryCardResponse atualizarStatus(@PathVariable Long pedidoId, @RequestBody DeliveryStatusRequest request) {
        Usuario entregador = requireEntregador();
        Pedido pedido = pedidoService.atualizarStatusEntrega(
            entregador,
            pedidoId,
            request.statusEntrega(),
            request.assinaturaEntrega(),
            request.comprovanteEntrega(),
            request.ocorrenciaEntrega()
        );
        return toResponse(pedido);
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        Usuario entregador = requireEntregador();
        SseEmitter emitter = notificationService.subscribe(entregador.getId());
        notificationService.send(entregador.getId(), "sync", new PedidoService.DeliveryNotification(0L, "Conexão SSE estabelecida", "ONLINE"));
        return emitter;
    }

    private Usuario requireEntregador() {
        Usuario usuario = autenticacaoService.getUsuarioAtual().orElse(null);
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.");
        }
        if (usuario.getPapel() != PapelUsuario.ENTREGADOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso restrito ao módulo de entregadores.");
        }
        return usuario;
    }

    private DeliveryCardResponse toResponse(Pedido pedido) {
        return new DeliveryCardResponse(
            pedido.getId(),
            "FF-" + pedido.getId(),
            pedido.getCliente() != null ? pedido.getCliente().getNome() : "Cliente",
            pedido.getStatusEntrega(),
            pedido.getPrazoEntrega(),
            pedido.getRegiaoEntrega(),
            pedido.getCepEntrega(),
            pedido.getLogradouroEntrega(),
            pedido.getNumeroEntrega(),
            pedido.getComplementoEntrega(),
            pedido.getBairroEntrega(),
            pedido.getCidadeEntrega(),
            pedido.getEstadoEntrega(),
            pedido.getValorFrete(),
            pedido.getDataPedido(),
            pedido.getEntregueEm(),
            pedido.getOcorrenciaEntrega()
        );
    }

    public record DeliveryStatusRequest(
        StatusEntrega statusEntrega,
        String assinaturaEntrega,
        String comprovanteEntrega,
        String ocorrenciaEntrega
    ) {}

    public record DeliveryCardResponse(
        Long id,
        String codigoPedido,
        String clienteNome,
        StatusEntrega statusEntrega,
        LocalDateTime prazoEntrega,
        String regiaoEntrega,
        String cepEntrega,
        String logradouroEntrega,
        String numeroEntrega,
        String complementoEntrega,
        String bairroEntrega,
        String cidadeEntrega,
        String estadoEntrega,
        java.math.BigDecimal valorFrete,
        LocalDateTime dataPedido,
        LocalDateTime entregueEm,
        String ocorrenciaEntrega
    ) {}
}

