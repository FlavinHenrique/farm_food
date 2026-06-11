package com.foodfarmer.foodfarmer.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodfarmer.foodfarmer.model.Loja;
import com.foodfarmer.foodfarmer.model.PapelUsuario;
import com.foodfarmer.foodfarmer.model.Pedido;
import com.foodfarmer.foodfarmer.model.Produto;
import com.foodfarmer.foodfarmer.model.StatusEntrega;
import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.repository.PedidoRepository;
import com.foodfarmer.foodfarmer.repository.ProdutoRepository;
import com.foodfarmer.foodfarmer.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ClienteService clienteService;
    @Mock
    private EntregadorNotificationService entregadorNotificationService;
    @Mock
    private PagamentoService pagamentoService;

    private PedidoService pedidoService;

    @BeforeEach
    void setUp() {
        pedidoService = new PedidoService(
            pedidoRepository,
            produtoRepository,
            usuarioRepository,
            clienteService,
            entregadorNotificationService,
            pagamentoService
        );
    }

    @Test
    void criarPedidosCheckoutDeveGerarPedidoAtribuidoENotificarEntregador() {
        Usuario cliente = usuario(1L, "Cliente", PapelUsuario.CLIENTE);
        Usuario entregador = usuario(2L, "Entregador", PapelUsuario.ENTREGADOR);
        Loja loja = new Loja(10L, "Horta", "Descricao", "img", null, null, null, null, false, false, usuario(3L, "Produtor", PapelUsuario.PRODUTOR), null);
        Produto produto = new Produto(7L, "Tomate", "Fresco", new BigDecimal("12.50"), "img", "kg", null, false, 0, true, null, loja);

        when(produtoRepository.findById(7L)).thenReturn(Optional.of(produto));
        when(usuarioRepository.findByPapel(PapelUsuario.ENTREGADOR)).thenReturn(List.of(entregador));
        AtomicLong idSequence = new AtomicLong(100L);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido saved = invocation.getArgument(0);
            saved.setId(idSequence.getAndIncrement());
            return saved;
        });

        PedidoService.CheckoutOrderRequest request = new PedidoService.CheckoutOrderRequest(
            "Cliente",
            "cliente@teste.com",
            "000.000.000-00",
            "(11) 99999-0000",
            null,
            true,
            "Casa",
            "01001000",
            "Rua A",
            "100",
            "Apto 1",
            "Centro",
            "Sao Paulo",
            "SP",
            "pix",
            new BigDecimal("15.90"),
            new BigDecimal("2.00"),
            List.of(new PedidoService.CheckoutItemRequest(7L, 2))
        );

        List<Pedido> pedidos = pedidoService.criarPedidosCheckout(cliente, request);

        assertEquals(1, pedidos.size());
        Pedido pedido = pedidos.get(0);
        assertEquals(entregador, pedido.getEntregador());
        assertEquals(StatusEntrega.PENDENTE, pedido.getStatusEntrega());
        assertEquals("Centro", pedido.getRegiaoEntrega());
        assertEquals(new BigDecimal("38.90"), pedido.getValorTotal());
        assertNotNull(pedido.getItens());
        assertEquals(1, pedido.getItens().size());
        verify(clienteService).salvarEndereco(any());
        verify(entregadorNotificationService).send(eq(2L), eq("novo-pedido"), any(PedidoService.DeliveryNotification.class));
    }

    @Test
    void listarPedidosEntregadorDeveAplicarFiltrosDeHistoricoStatusERegiao() {
        Usuario entregador = usuario(2L, "Entregador", PapelUsuario.ENTREGADOR);

        Pedido entregueCentro = pedido(entregador, StatusEntrega.ENTREGUE, "Centro", LocalDateTime.now().minusHours(1));
        Pedido problemaZonaSul = pedido(entregador, StatusEntrega.PROBLEMA, "Zona Sul", LocalDateTime.now().plusHours(2));
        Pedido pendenteCentro = pedido(entregador, StatusEntrega.PENDENTE, "Centro", LocalDateTime.now().plusHours(3));

        when(pedidoRepository.findByEntregadorOrderByPrazoEntregaAsc(entregador))
            .thenReturn(List.of(entregueCentro, problemaZonaSul, pendenteCentro));

        List<Pedido> pedidos = pedidoService.listarPedidosEntregador(
            entregador,
            "CONCLUIDO",
            "centro",
            LocalDateTime.now().plusHours(1),
            true
        );

        assertEquals(1, pedidos.size());
        assertEquals(StatusEntrega.ENTREGUE, pedidos.get(0).getStatusEntrega());
        assertEquals("Centro", pedidos.get(0).getRegiaoEntrega());
    }

    @Test
    void atualizarStatusEntregaDeveRegistrarEntregaEEnviarNotificacao() {
        Usuario entregador = usuario(2L, "Entregador", PapelUsuario.ENTREGADOR);
        Pedido pedido = pedido(entregador, StatusEntrega.CHEGOU_DESTINO, "Centro", LocalDateTime.now().plusMinutes(20));
        pedido.setId(55L);

        when(pedidoRepository.findById(55L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido atualizado = pedidoService.atualizarStatusEntrega(
            entregador,
            55L,
            StatusEntrega.ENTREGUE,
            "Maria Silva",
            "base64:image",
            ""
        );

        assertEquals(StatusEntrega.ENTREGUE, atualizado.getStatusEntrega());
        assertEquals("Finalizado", atualizado.getStatus());
        assertEquals("Maria Silva", atualizado.getAssinaturaEntrega());
        assertEquals("base64:image", atualizado.getComprovanteEntrega());
        assertNotNull(atualizado.getEntregueEm());
        verify(entregadorNotificationService).send(eq(2L), eq("status-entrega"), any(PedidoService.DeliveryNotification.class));
    }

    @Test
    void atualizarStatusEntregaDeveFalharQuandoPedidoPertenceAOutroEntregador() {
        Usuario entregador = usuario(2L, "Entregador", PapelUsuario.ENTREGADOR);
        Usuario outroEntregador = usuario(3L, "Outro", PapelUsuario.ENTREGADOR);
        Pedido pedido = pedido(outroEntregador, StatusEntrega.PENDENTE, "Centro", LocalDateTime.now().plusHours(1));
        pedido.setId(77L);

        when(pedidoRepository.findById(77L)).thenReturn(Optional.of(pedido));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () ->
            pedidoService.atualizarStatusEntrega(entregador, 77L, StatusEntrega.ACEITO, "", "", "")
        );

        assertTrue(error.getMessage().contains("atribuido"));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    private Usuario usuario(Long id, String nome, PapelUsuario papel) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome(nome);
        usuario.setEmail(nome.toLowerCase().replace(' ', '.') + "@teste.com");
        usuario.setSenha("123");
        usuario.setPapel(papel);
        return usuario;
    }

    private Pedido pedido(Usuario entregador, StatusEntrega statusEntrega, String regiao, LocalDateTime prazo) {
        Pedido pedido = new Pedido();
        pedido.setEntregador(entregador);
        pedido.setStatusEntrega(statusEntrega);
        pedido.setRegiaoEntrega(regiao);
        pedido.setPrazoEntrega(prazo);
        pedido.setValorFrete(new BigDecimal("10.00"));
        pedido.setDataPedido(LocalDateTime.now().minusHours(2));
        return pedido;
    }
}
