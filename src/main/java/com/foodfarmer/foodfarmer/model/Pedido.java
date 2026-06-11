package com.foodfarmer.foodfarmer.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Usuario cliente;
    
    @ManyToOne
    @JoinColumn(name = "loja_id")
    private Loja loja;
    
    private LocalDateTime dataPedido;
    private BigDecimal valorTotal;
    private BigDecimal valorProdutos; // soma dos preços dos produtos
    private BigDecimal valorDesconto; // valor do desconto aplicado
    private String status; // ex: "Pendente", "Pago", "Finalizado", "Cancelado"

    @ManyToOne
    @JoinColumn(name = "entregador_id")
    private Usuario entregador;

    @Enumerated(EnumType.STRING)
    private StatusEntrega statusEntrega;

    @Enumerated(EnumType.STRING)
    private TipoMetodoPagamento tipoMetodoPagamento;

    @Enumerated(EnumType.STRING)
    private StatusPagamento statusPagamento;

    private boolean repasseLiberado; // se o valor já pode ser repassado ao produtor

    private LocalDateTime prazoEntrega;
    private LocalDateTime inicioRotaEm;
    private LocalDateTime chegadaDestinoEm;
    private LocalDateTime entregueEm;

    private String regiaoEntrega;
    private String cepEntrega;
    private String logradouroEntrega;
    private String numeroEntrega;
    private String complementoEntrega;
    private String bairroEntrega;
    private String cidadeEntrega;
    private String estadoEntrega;
    private BigDecimal valorFrete;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String comprovanteEntrega;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String assinaturaEntrega;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String ocorrenciaEntrega;
    
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemPedido> itens;

    public Pedido() {}

    public Pedido(Long id, Usuario cliente, Loja loja, LocalDateTime dataPedido, BigDecimal valorTotal, String status) {
        this.id = id;
        this.cliente = cliente;
        this.loja = loja;
        this.dataPedido = dataPedido;
        this.valorTotal = valorTotal;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getCliente() { return cliente; }
    public void setCliente(Usuario cliente) { this.cliente = cliente; }
    public Loja getLoja() { return loja; }
    public void setLoja(Loja loja) { this.loja = loja; }
    public LocalDateTime getDataPedido() { return dataPedido; }
    public void setDataPedido(LocalDateTime dataPedido) { this.dataPedido = dataPedido; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public BigDecimal getValorProdutos() { return valorProdutos; }
    public void setValorProdutos(BigDecimal valorProdutos) { this.valorProdutos = valorProdutos; }
    public BigDecimal getValorDesconto() { return valorDesconto; }
    public void setValorDesconto(BigDecimal valorDesconto) { this.valorDesconto = valorDesconto; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public TipoMetodoPagamento getTipoMetodoPagamento() { return tipoMetodoPagamento; }
    public void setTipoMetodoPagamento(TipoMetodoPagamento tipoMetodoPagamento) { this.tipoMetodoPagamento = tipoMetodoPagamento; }
    public StatusPagamento getStatusPagamento() { return statusPagamento; }
    public void setStatusPagamento(StatusPagamento statusPagamento) { this.statusPagamento = statusPagamento; }
    public boolean isRepasseLiberado() { return repasseLiberado; }
    public void setRepasseLiberado(boolean repasseLiberado) { this.repasseLiberado = repasseLiberado; }
    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }
    public Usuario getEntregador() { return entregador; }
    public void setEntregador(Usuario entregador) { this.entregador = entregador; }
    public StatusEntrega getStatusEntrega() { return statusEntrega; }
    public void setStatusEntrega(StatusEntrega statusEntrega) { this.statusEntrega = statusEntrega; }
    public LocalDateTime getPrazoEntrega() { return prazoEntrega; }
    public void setPrazoEntrega(LocalDateTime prazoEntrega) { this.prazoEntrega = prazoEntrega; }
    public LocalDateTime getInicioRotaEm() { return inicioRotaEm; }
    public void setInicioRotaEm(LocalDateTime inicioRotaEm) { this.inicioRotaEm = inicioRotaEm; }
    public LocalDateTime getChegadaDestinoEm() { return chegadaDestinoEm; }
    public void setChegadaDestinoEm(LocalDateTime chegadaDestinoEm) { this.chegadaDestinoEm = chegadaDestinoEm; }
    public LocalDateTime getEntregueEm() { return entregueEm; }
    public void setEntregueEm(LocalDateTime entregueEm) { this.entregueEm = entregueEm; }
    public String getRegiaoEntrega() { return regiaoEntrega; }
    public void setRegiaoEntrega(String regiaoEntrega) { this.regiaoEntrega = regiaoEntrega; }
    public String getCepEntrega() { return cepEntrega; }
    public void setCepEntrega(String cepEntrega) { this.cepEntrega = cepEntrega; }
    public String getLogradouroEntrega() { return logradouroEntrega; }
    public void setLogradouroEntrega(String logradouroEntrega) { this.logradouroEntrega = logradouroEntrega; }
    public String getNumeroEntrega() { return numeroEntrega; }
    public void setNumeroEntrega(String numeroEntrega) { this.numeroEntrega = numeroEntrega; }
    public String getComplementoEntrega() { return complementoEntrega; }
    public void setComplementoEntrega(String complementoEntrega) { this.complementoEntrega = complementoEntrega; }
    public String getBairroEntrega() { return bairroEntrega; }
    public void setBairroEntrega(String bairroEntrega) { this.bairroEntrega = bairroEntrega; }
    public String getCidadeEntrega() { return cidadeEntrega; }
    public void setCidadeEntrega(String cidadeEntrega) { this.cidadeEntrega = cidadeEntrega; }
    public String getEstadoEntrega() { return estadoEntrega; }
    public void setEstadoEntrega(String estadoEntrega) { this.estadoEntrega = estadoEntrega; }
    public BigDecimal getValorFrete() { return valorFrete; }
    public void setValorFrete(BigDecimal valorFrete) { this.valorFrete = valorFrete; }
    public String getComprovanteEntrega() { return comprovanteEntrega; }
    public void setComprovanteEntrega(String comprovanteEntrega) { this.comprovanteEntrega = comprovanteEntrega; }
    public String getAssinaturaEntrega() { return assinaturaEntrega; }
    public void setAssinaturaEntrega(String assinaturaEntrega) { this.assinaturaEntrega = assinaturaEntrega; }
    public String getOcorrenciaEntrega() { return ocorrenciaEntrega; }
    public void setOcorrenciaEntrega(String ocorrenciaEntrega) { this.ocorrenciaEntrega = ocorrenciaEntrega; }
}
