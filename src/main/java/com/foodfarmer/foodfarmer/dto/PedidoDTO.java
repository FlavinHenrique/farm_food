package com.foodfarmer.foodfarmer.dto;

import java.math.BigDecimal;
import java.util.List;

public class PedidoDTO {
    private List<ItemPedidoDTO> itens;
    private BigDecimal valorTotal;
    private String status;
    private String cepEntrega;
    private String logradouroEntrega;
    private String numeroEntrega;
    private String complementoEntrega;
    private String bairroEntrega;
    private String cidadeEntrega;
    private String estadoEntrega;
    private String regiaoEntrega;

    public List<ItemPedidoDTO> getItens() { return itens; }
    public void setItens(List<ItemPedidoDTO> itens) { this.itens = itens; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
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
    public String getRegiaoEntrega() { return regiaoEntrega; }
    public void setRegiaoEntrega(String regiaoEntrega) { this.regiaoEntrega = regiaoEntrega; }
}
