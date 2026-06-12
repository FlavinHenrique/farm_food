package com.foodfarmer.foodfarmer.service;

import com.foodfarmer.foodfarmer.model.*;
import com.foodfarmer.foodfarmer.repository.PedidoRepository;
import com.foodfarmer.foodfarmer.repository.TransacaoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PagamentoService {

    private final TransacaoRepository transacaoRepository;
    private final PedidoRepository pedidoRepository;

    // Taxa de comissão padrão (10%
    private static final BigDecimal TAXA_COMISSAO = new BigDecimal("0.10");

    public PagamentoService(TransacaoRepository transacaoRepository, PedidoRepository pedidoRepository) {
        this.transacaoRepository = transacaoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public Transacao criarTransacao(Pedido pedido, TipoMetodoPagamento tipoMetodo) {
        Transacao transacao = new Transacao();
        transacao.setPedido(pedido);
        transacao.setTipoMetodo(tipoMetodo);
        transacao.setStatus(StatusPagamento.PENDENTE);
        transacao.setValorTotal(pedido.getValorTotal());
        transacao.setValorProdutos(pedido.getValorProdutos());
        transacao.setValorFrete(pedido.getValorFrete());
        transacao.setValorDesconto(pedido.getValorDesconto());

        // Calcular comissão e repasse
        BigDecimal comissao = pedido.getValorProdutos().multiply(TAXA_COMISSAO).setScale(2, RoundingMode.HALF_UP);
        transacao.setComissaoPlataforma(comissao);
        BigDecimal repasse = pedido.getValorProdutos().subtract(comissao);
        transacao.setValorRepasseProdutor(repasse);

        transacao.setDataCriacao(LocalDateTime.now());
        transacao.setCodigoPagamento(UUID.randomUUID().toString());

        return transacaoRepository.save(transacao);
    }

    public Transacao confirmarPagamento(Long transacaoId) {
        Optional<Transacao> transacaoOpt = transacaoRepository.findById(transacaoId);
        if (transacaoOpt.isEmpty()) {
            throw new IllegalArgumentException("Transação não encontrada");
        }

        Transacao transacao = transacaoOpt.get();
        transacao.setStatus(StatusPagamento.PAGO);
        transacao.setDataConfirmacao(LocalDateTime.now());

        Pedido pedido = transacao.getPedido();
        pedido.setStatusPagamento(StatusPagamento.PAGO);
        pedido.setStatus("Pago");
        pedidoRepository.save(pedido);

        return transacaoRepository.save(transacao);
    }

    public Transacao cancelarPagamento(Long transacaoId) {
        Optional<Transacao> transacaoOpt = transacaoRepository.findById(transacaoId);
        if (transacaoOpt.isEmpty()) {
            throw new IllegalArgumentException("Transação não encontrada");
        }

        Transacao transacao = transacaoOpt.get();
        transacao.setStatus(StatusPagamento.CANCELADO);

        Pedido pedido = transacao.getPedido();
        pedido.setStatusPagamento(StatusPagamento.CANCELADO);
        pedido.setStatus("Cancelado");
        pedidoRepository.save(pedido);

        return transacaoRepository.save(transacao);
    }

    public Transacao estornarPagamento(Long transacaoId) {
        Optional<Transacao> transacaoOpt = transacaoRepository.findById(transacaoId);
        if (transacaoOpt.isEmpty()) {
            throw new IllegalArgumentException("Transação não encontrada");
        }

        Transacao transacao = transacaoOpt.get();
        transacao.setStatus(StatusPagamento.ESTORNADO);

        Pedido pedido = transacao.getPedido();
        pedido.setStatusPagamento(StatusPagamento.ESTORNADO);
        pedido.setStatus("Cancelado");
        pedido.setRepasseLiberado(false);
        pedidoRepository.save(pedido);

        return transacaoRepository.save(transacao);
    }

    public void liberarRepasseProdutor(Long pedidoId) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("Pedido não encontrado");
        }

        Pedido pedido = pedidoOpt.get();
        if (pedido.getStatusEntrega() == StatusEntrega.ENTREGUE) {
            pedido.setRepasseLiberado(true);
            pedidoRepository.save(pedido);
        }
    }

    public Optional<Transacao> getTransacaoPorPedido(Long pedidoId) {
        return transacaoRepository.findByPedidoId(pedidoId);
    }
}
