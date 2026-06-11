package com.foodfarmer.foodfarmer.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacoes")
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    private TipoMetodoPagamento tipoMetodo;

    @Enumerated(EnumType.STRING)
    private StatusPagamento status;

    private BigDecimal valorTotal;
    private BigDecimal valorProdutos;
    private BigDecimal valorFrete;
    private BigDecimal valorDesconto;
    private BigDecimal comissaoPlataforma; // comissão do Farm Food
    private BigDecimal valorRepasseProdutor; // valor que vai para o produtor

    private LocalDateTime dataCriacao;
    private LocalDateTime dataConfirmacao;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String codigoPagamento; // ex: código Pix, identificador da transação do gateway
    @Lob
    @Column(columnDefinition = "TEXT")
    private String gatewayResponse; // resposta do gateway de pagamento

    public Transacao() {}

    public Transacao(Long id, Pedido pedido, TipoMetodoPagamento tipoMetodo, StatusPagamento status,
                     BigDecimal valorTotal, BigDecimal valorProdutos, BigDecimal valorFrete, BigDecimal valorDesconto,
                     BigDecimal comissaoPlataforma, BigDecimal valorRepasseProdutor, LocalDateTime dataCriacao,
                     LocalDateTime dataConfirmacao, String codigoPagamento, String gatewayResponse) {
        this.id = id;
        this.pedido = pedido;
        this.tipoMetodo = tipoMetodo;
        this.status = status;
        this.valorTotal = valorTotal;
        this.valorProdutos = valorProdutos;
        this.valorFrete = valorFrete;
        this.valorDesconto = valorDesconto;
        this.comissaoPlataforma = comissaoPlataforma;
        this.valorRepasseProdutor = valorRepasseProdutor;
        this.dataCriacao = dataCriacao;
        this.dataConfirmacao = dataConfirmacao;
        this.codigoPagamento = codigoPagamento;
        this.gatewayResponse = gatewayResponse;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public TipoMetodoPagamento getTipoMetodo() { return tipoMetodo; }
    public void setTipoMetodo(TipoMetodoPagamento tipoMetodo) { this.tipoMetodo = tipoMetodo; }
    public StatusPagamento getStatus() { return status; }
    public void setStatus(StatusPagamento status) { this.status = status; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public BigDecimal getValorProdutos() { return valorProdutos; }
    public void setValorProdutos(BigDecimal valorProdutos) { this.valorProdutos = valorProdutos; }
    public BigDecimal getValorFrete() { return valorFrete; }
    public void setValorFrete(BigDecimal valorFrete) { this.valorFrete = valorFrete; }
    public BigDecimal getValorDesconto() { return valorDesconto; }
    public void setValorDesconto(BigDecimal valorDesconto) { this.valorDesconto = valorDesconto; }
    public BigDecimal getComissaoPlataforma() { return comissaoPlataforma; }
    public void setComissaoPlataforma(BigDecimal comissaoPlataforma) { this.comissaoPlataforma = comissaoPlataforma; }
    public BigDecimal getValorRepasseProdutor() { return valorRepasseProdutor; }
    public void setValorRepasseProdutor(BigDecimal valorRepasseProdutor) { this.valorRepasseProdutor = valorRepasseProdutor; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDateTime getDataConfirmacao() { return dataConfirmacao; }
    public void setDataConfirmacao(LocalDateTime dataConfirmacao) { this.dataConfirmacao = dataConfirmacao; }
    public String getCodigoPagamento() { return codigoPagamento; }
    public void setCodigoPagamento(String codigoPagamento) { this.codigoPagamento = codigoPagamento; }
    public String getGatewayResponse() { return gatewayResponse; }
    public void setGatewayResponse(String gatewayResponse) { this.gatewayResponse = gatewayResponse; }
}
