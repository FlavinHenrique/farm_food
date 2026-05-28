package com.foodfarmer.foodfarmer.model;

import jakarta.persistence.*;

@Entity
@Table(name = "metodos_pagamento")
public class MetodoPagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nomeTitular;
    private String numeroCartao; 
    private String dataValidade;
    private String bandeira; // Visa, Master, etc.
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public MetodoPagamento() {}

    public MetodoPagamento(Long id, String nomeTitular, String numeroCartao, String dataValidade, String bandeira, Usuario usuario) {
        this.id = id;
        this.nomeTitular = nomeTitular;
        this.numeroCartao = numeroCartao;
        this.dataValidade = dataValidade;
        this.bandeira = bandeira;
        this.usuario = usuario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomeTitular() { return nomeTitular; }
    public void setNomeTitular(String nomeTitular) { this.nomeTitular = nomeTitular; }
    public String getNumeroCartao() { return numeroCartao; }
    public void setNumeroCartao(String numeroCartao) { this.numeroCartao = numeroCartao; }
    public String getDataValidade() { return dataValidade; }
    public void setDataValidade(String dataValidade) { this.dataValidade = dataValidade; }
    public String getBandeira() { return bandeira; }
    public void setBandeira(String bandeira) { this.bandeira = bandeira; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
