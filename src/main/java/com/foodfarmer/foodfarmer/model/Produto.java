package com.foodfarmer.foodfarmer.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String urlImagem;
    private String unidade; // ex: "kg", "un"
    
    private boolean emPromocao;
    private Integer percentualDesconto;
    
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
    
    @ManyToOne
    @JoinColumn(name = "loja_id")
    private Loja loja;

    public Produto(Long id, String nome, String descricao, BigDecimal preco, String urlImagem, String unidade, boolean emPromocao, Integer percentualDesconto, Categoria categoria, Loja loja) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.urlImagem = urlImagem;
        this.unidade = unidade;
        this.emPromocao = emPromocao;
        this.percentualDesconto = percentualDesconto;
        this.categoria = categoria;
        this.loja = loja;
    }

    public Produto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public String getUrlImagem() { return urlImagem; }
    public void setUrlImagem(String urlImagem) { this.urlImagem = urlImagem; }
    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }
    public boolean isEmPromocao() { return emPromocao; }
    public void setEmPromocao(boolean emPromocao) { this.emPromocao = emPromocao; }
    public Integer getPercentualDesconto() { return percentualDesconto; }
    public void setPercentualDesconto(Integer percentualDesconto) { this.percentualDesconto = percentualDesconto; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public Loja getLoja() { return loja; }
    public void setLoja(Loja loja) { this.loja = loja; }
}
