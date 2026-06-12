package com.foodfarmer.foodfarmer.service;

import com.foodfarmer.foodfarmer.model.*;
import com.foodfarmer.foodfarmer.repository.ProdutoRepository;
import com.foodfarmer.foodfarmer.repository.PedidoRepository;
import com.foodfarmer.foodfarmer.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteService clienteService;
    private final EntregadorNotificationService entregadorNotificationService;
    private final PagamentoService pagamentoService;

    public PedidoService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository, UsuarioRepository usuarioRepository,
            ClienteService clienteService, EntregadorNotificationService entregadorNotificationService, PagamentoService pagamentoService) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
        this.clienteService = clienteService;
        this.entregadorNotificationService = entregadorNotificationService;
        this.pagamentoService = pagamentoService;
    }

    public List<Pedido> getPedidosPorLojas(List<Loja> lojas) {
        return pedidoRepository.findByLojaIn(lojas);
    }

    public List<Pedido> getPedidosPorCliente(Usuario cliente) {
        return pedidoRepository.findByClienteOrderByDataPedidoDesc(cliente);
    }

    public Pedido getPedidoPorId(Long pedidoId) {
        return pedidoRepository.findById(pedidoId).orElse(null);
    }

    public BigDecimal calcularFaturamentoTotal(List<Pedido> pedidos) {
        return pedidos.stream()
                .map(Pedido::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public void salvarPedido(Pedido pedido) {
        pedidoRepository.save(pedido);
    }

    @Transactional
    public List<Pedido> criarPedidosCheckout(Usuario cliente, CheckoutOrderRequest request) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente inválido.");
        }
        if (request == null || request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("Carrinho vazio.");
        }

        Map<Loja, List<CheckoutResolvedItem>> itensPorLoja = new LinkedHashMap<>();
        BigDecimal subtotalGeral = BigDecimal.ZERO;

        for (CheckoutItemRequest itemRequest : request.items()) {
            Produto produto = produtoRepository.findById(itemRequest.productId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + itemRequest.productId()));

            int quantidade = Math.max(1, Optional.ofNullable(itemRequest.quantity()).orElse(1));
            BigDecimal precoUnitario = produto.getPrecoComDesconto();
            BigDecimal subtotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
            subtotalGeral = subtotalGeral.add(subtotal);

            itensPorLoja.computeIfAbsent(produto.getLoja(), ignored -> new ArrayList<>())
                .add(new CheckoutResolvedItem(produto, quantidade, precoUnitario, subtotal));
        }

        BigDecimal shipping = safeMoney(request.shipping()).max(BigDecimal.ZERO);
        BigDecimal discount = safeMoney(request.discount()).max(BigDecimal.ZERO);

        List<Pedido> pedidos = new ArrayList<>();
        BigDecimal freteDistribuido = BigDecimal.ZERO;
        BigDecimal descontoDistribuido = BigDecimal.ZERO;
        int index = 0;
        for (Map.Entry<Loja, List<CheckoutResolvedItem>> entry : itensPorLoja.entrySet()) {
            Loja loja = entry.getKey();
            List<CheckoutResolvedItem> itens = entry.getValue();
            BigDecimal subtotalLoja = itens.stream().map(CheckoutResolvedItem::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal proporcao = subtotalGeral.signum() == 0 ? BigDecimal.ZERO : subtotalLoja.divide(subtotalGeral, 6, RoundingMode.HALF_UP);

            BigDecimal fretePedido = (index == itensPorLoja.size() - 1)
                ? shipping.subtract(freteDistribuido)
                : shipping.multiply(proporcao).setScale(2, RoundingMode.HALF_UP);

            BigDecimal descontoPedido = (index == itensPorLoja.size() - 1)
                ? discount.subtract(descontoDistribuido)
                : discount.multiply(proporcao).setScale(2, RoundingMode.HALF_UP);

            Pedido pedido = new Pedido();
            pedido.setCliente(cliente);
            pedido.setLoja(loja);
            pedido.setDataPedido(LocalDateTime.now());
            pedido.setStatus("Pendente");
            pedido.setStatusEntrega(StatusEntrega.PENDENTE);
            pedido.setStatusPagamento(StatusPagamento.PENDENTE);
            pedido.setPrazoEntrega(LocalDateTime.now().plusHours(4 + index));
            pedido.setValorFrete(fretePedido.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP));
            pedido.setValorProdutos(subtotalLoja.setScale(2, RoundingMode.HALF_UP));
            pedido.setValorDesconto(descontoPedido.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP));

            preencherEnderecoEntrega(pedido, cliente, request);
            // Não atribui entregador automaticamente - pedido fica disponível para pegar

            BigDecimal valorTotal = subtotalLoja.add(pedido.getValorFrete()).subtract(descontoPedido.max(BigDecimal.ZERO));
            if (valorTotal.signum() < 0) {
                valorTotal = BigDecimal.ZERO;
            }
            pedido.setValorTotal(valorTotal.setScale(2, RoundingMode.HALF_UP));

            // Define método de pagamento
            TipoMetodoPagamento tipoMetodo = TipoMetodoPagamento.PIX;
            if (request.metodoPagamento() != null) {
                try {
                    tipoMetodo = TipoMetodoPagamento.valueOf(request.metodoPagamento().toUpperCase());
                } catch (IllegalArgumentException ignored) {}
            }
            pedido.setTipoMetodoPagamento(tipoMetodo);

            List<ItemPedido> itensPedido = itens.stream().map(item -> {
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setPedido(pedido);
                itemPedido.setProduto(item.produto());
                itemPedido.setQuantidade(item.quantidade());
                itemPedido.setPrecoUnitario(item.precoUnitario());
                return itemPedido;
            }).toList();
            pedido.setItens(itensPedido);
            Pedido saved = pedidoRepository.save(pedido);
            pedidos.add(saved);

            // Cria transação para o pedido
            pagamentoService.criarTransacao(saved, tipoMetodo);
            freteDistribuido = freteDistribuido.add(pedido.getValorFrete() == null ? BigDecimal.ZERO : pedido.getValorFrete());
            descontoDistribuido = descontoDistribuido.add(descontoPedido.max(BigDecimal.ZERO));
            index++;
        }

        if (request.salvarNovoEndereco()) {
            salvarEnderecoCheckout(cliente, request);
        }

        return pedidos;
    }

    public List<Pedido> listarPedidosDisponiveis() {
        return pedidoRepository.findByEntregadorIsNullAndStatusEntregaOrderByDataPedidoDesc(StatusEntrega.PENDENTE);
    }

    public List<Pedido> listarPedidosEntregador(Usuario entregador, String filtroStatus, String regiao, LocalDateTime prazoAntesDe, boolean somenteHistorico) {
        List<Pedido> pedidos = pedidoRepository.findByEntregadorOrderByPrazoEntregaAsc(entregador);

        return pedidos.stream()
            .filter(p -> !somenteHistorico || isHistorico(p.getStatusEntrega()))
            .filter(p -> applyFiltroStatus(p.getStatusEntrega(), filtroStatus))
            .filter(p -> !StringUtils.hasText(regiao) || containsIgnoreCase(p.getRegiaoEntrega(), regiao))
            .filter(p -> prazoAntesDe == null || (p.getPrazoEntrega() != null && !p.getPrazoEntrega().isAfter(prazoAntesDe)))
            .sorted(Comparator.comparing(Pedido::getPrazoEntrega, Comparator.nullsLast(Comparator.naturalOrder())))
            .toList();
    }

    @Transactional
    public Pedido aceitarPedidoDisponivel(Usuario entregador, Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getEntregador() != null) {
            throw new IllegalArgumentException("Este pedido já foi aceito por outro entregador.");
        }
        if (pedido.getStatusEntrega() != StatusEntrega.PENDENTE) {
            throw new IllegalArgumentException("Este pedido não está disponível para aceitar.");
        }

        pedido.setEntregador(entregador);
        pedido.setStatusEntrega(StatusEntrega.ACEITO);
        pedido.setStatus("Aceito para entrega");

        Pedido saved = pedidoRepository.save(pedido);
        if (saved.getEntregador() != null && saved.getEntregador().getId() != null) {
            entregadorNotificationService.send(saved.getEntregador().getId(), "pedido-aceito", new DeliveryNotification(saved.getId(), "Pedido aceito!", saved.getStatusEntrega().name()));
        }
        return saved;
    }

    @Transactional
    public Pedido atualizarStatusEntrega(Usuario entregador, Long pedidoId, StatusEntrega novoStatus, String assinatura, String comprovante, String ocorrencia) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getEntregador() == null || !pedido.getEntregador().getId().equals(entregador.getId())) {
            throw new IllegalArgumentException("Este pedido não está atribuído ao entregador.");
        }
        if (novoStatus == null) {
            throw new IllegalArgumentException("Status de entrega inválido.");
        }
        validarTransicao(pedido.getStatusEntrega(), novoStatus);

        pedido.setStatusEntrega(novoStatus);
        if (StringUtils.hasText(ocorrencia)) {
            pedido.setOcorrenciaEntrega(ocorrencia);
        }
        if (StringUtils.hasText(assinatura)) {
            pedido.setAssinaturaEntrega(assinatura);
        }
        if (StringUtils.hasText(comprovante)) {
            pedido.setComprovanteEntrega(comprovante);
        }

        LocalDateTime agora = LocalDateTime.now();
        switch (novoStatus) {
            case ACEITO -> pedido.setStatus("Aceito para entrega");
            case EM_ROTA -> {
                pedido.setStatus("Em rota");
                pedido.setInicioRotaEm(agora);
            }
            case CHEGOU_DESTINO -> {
                pedido.setStatus("Entregador no destino");
                pedido.setChegadaDestinoEm(agora);
            }
            case ENTREGUE -> {
                pedido.setStatus("Finalizado");
                pedido.setEntregueEm(agora);
                pagamentoService.liberarRepasseProdutor(pedido.getId());
            }
            case PROBLEMA -> pedido.setStatus("Problema na entrega");
            case CANCELADO -> pedido.setStatus("Cancelado");
            default -> pedido.setStatus("Pendente");
        }

        Pedido saved = pedidoRepository.save(pedido);
        if (saved.getEntregador() != null && saved.getEntregador().getId() != null) {
            entregadorNotificationService.send(saved.getEntregador().getId(), "status-entrega", new DeliveryNotification(saved.getId(), "Status atualizado", saved.getStatusEntrega().name()));
        }
        return saved;
    }

    private void salvarEnderecoCheckout(Usuario cliente, CheckoutOrderRequest request) {
        Endereco endereco = new Endereco();
        endereco.setApelido(StringUtils.hasText(request.apelidoEndereco()) ? request.apelidoEndereco() : "Entrega");
        endereco.setRua(request.logradouro());
        endereco.setNumero(request.numero());
        endereco.setComplemento(request.complemento());
        endereco.setBairro(request.bairro());
        endereco.setCidade(request.cidade());
        endereco.setEstado(request.estado());
        endereco.setCep(request.cep());
        endereco.setUsuario(cliente);
        clienteService.salvarEndereco(endereco);
    }

    private void preencherEnderecoEntrega(Pedido pedido, Usuario cliente, CheckoutOrderRequest request) {
        pedido.setCepEntrega(request.cep());
        pedido.setLogradouroEntrega(request.logradouro());
        pedido.setNumeroEntrega(request.numero());
        pedido.setComplementoEntrega(request.complemento());
        pedido.setBairroEntrega(request.bairro());
        pedido.setCidadeEntrega(request.cidade());
        pedido.setEstadoEntrega(request.estado());

        String regiao = StringUtils.hasText(request.bairro()) ? request.bairro() : request.cidade();
        if (!StringUtils.hasText(regiao) && request.enderecoId() != null) {
            clienteService.getEnderecoPorId(request.enderecoId()).ifPresent(end -> {
                pedido.setCepEntrega(end.getCep());
                pedido.setLogradouroEntrega(end.getRua());
                pedido.setNumeroEntrega(end.getNumero());
                pedido.setComplementoEntrega(end.getComplemento());
                pedido.setBairroEntrega(end.getBairro());
                pedido.setCidadeEntrega(end.getCidade());
                pedido.setEstadoEntrega(end.getEstado());
                pedido.setRegiaoEntrega(StringUtils.hasText(end.getBairro()) ? end.getBairro() : end.getCidade());
            });
        }

        if (!StringUtils.hasText(pedido.getRegiaoEntrega())) {
            pedido.setRegiaoEntrega(regiao);
        }

        if (!StringUtils.hasText(pedido.getCidadeEntrega())) {
            pedido.setCidadeEntrega("Cidade");
        }
        if (!StringUtils.hasText(pedido.getEstadoEntrega())) {
            pedido.setEstadoEntrega("UF");
        }
    }

    private Usuario escolherEntregador(String regiao) {
        List<Usuario> entregadores = usuarioRepository.findByPapel(PapelUsuario.ENTREGADOR);
        if (entregadores.isEmpty()) {
            return null;
        }
        return entregadores.get(0);
    }

    private boolean applyFiltroStatus(StatusEntrega statusEntrega, String filtroStatus) {
        if (!StringUtils.hasText(filtroStatus)) {
            return true;
        }
        if (statusEntrega == null) {
            return false;
        }
        String filtro = filtroStatus.trim().toUpperCase();
        return switch (filtro) {
            case "PENDENTE" -> statusEntrega == StatusEntrega.PENDENTE;
            case "EM_ANDAMENTO" -> statusEntrega.isEmAndamento();
            case "CONCLUIDO" -> statusEntrega.isConcluido();
            case "CANCELADO" -> statusEntrega.isCancelado();
            default -> statusEntrega.name().equals(filtro);
        };
    }

    private boolean isHistorico(StatusEntrega statusEntrega) {
        return statusEntrega == StatusEntrega.ENTREGUE || statusEntrega == StatusEntrega.CANCELADO || statusEntrega == StatusEntrega.PROBLEMA;
    }

    private void validarTransicao(StatusEntrega atual, StatusEntrega proximo) {
        if (atual == null || atual == proximo) {
            return;
        }
        if (EnumSet.of(StatusEntrega.ENTREGUE, StatusEntrega.CANCELADO).contains(atual)) {
            throw new IllegalArgumentException("A entrega já foi finalizada.");
        }

        boolean permitido = switch (atual) {
            case PENDENTE -> EnumSet.of(StatusEntrega.ACEITO, StatusEntrega.CANCELADO).contains(proximo);
            case ACEITO -> EnumSet.of(StatusEntrega.EM_ROTA, StatusEntrega.CANCELADO).contains(proximo);
            case EM_ROTA -> EnumSet.of(StatusEntrega.CHEGOU_DESTINO, StatusEntrega.PROBLEMA, StatusEntrega.CANCELADO).contains(proximo);
            case CHEGOU_DESTINO -> EnumSet.of(StatusEntrega.ENTREGUE, StatusEntrega.PROBLEMA, StatusEntrega.CANCELADO).contains(proximo);
            case PROBLEMA -> EnumSet.of(StatusEntrega.EM_ROTA, StatusEntrega.CANCELADO).contains(proximo);
            default -> false;
        };

        if (!permitido) {
            throw new IllegalArgumentException("Transição de status inválida.");
        }
    }

    private boolean containsIgnoreCase(String value, String part) {
        return value != null && value.toLowerCase().contains(part.toLowerCase());
    }

    private BigDecimal safeMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private record CheckoutResolvedItem(Produto produto, int quantidade, BigDecimal precoUnitario, BigDecimal subtotal) {}

    public record DeliveryNotification(Long pedidoId, String mensagem, String statusEntrega) {}

    public record CheckoutItemRequest(Long productId, Integer quantity) {}

    public record CheckoutOrderRequest(
        String nome,
        String email,
        String cpf,
        String telefone,
        Long enderecoId,
        boolean salvarNovoEndereco,
        String apelidoEndereco,
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String metodoPagamento,
        BigDecimal shipping,
        BigDecimal discount,
        List<CheckoutItemRequest> items
    ) {}
}
