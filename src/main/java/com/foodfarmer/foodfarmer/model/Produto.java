package com.foodfarmer.foodfarmer.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
    private Integer estoque;
    
    private boolean emPromocao;
    private Integer percentualDesconto;
    private boolean ativo = true;
    
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
    
    @ManyToOne
    @JoinColumn(name = "loja_id")
    private Loja loja;

    public Produto(Long id, String nome, String descricao, BigDecimal preco, String urlImagem, 
                   String unidade, Integer estoque, boolean emPromocao, Integer percentualDesconto, 
                   boolean ativo, Categoria categoria, Loja loja) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.urlImagem = urlImagem;
        this.unidade = unidade;
        this.estoque = estoque;
        this.emPromocao = emPromocao;
        this.percentualDesconto = percentualDesconto;
        this.ativo = ativo;
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
    public Integer getEstoque() { return estoque; }
    public void setEstoque(Integer estoque) { this.estoque = estoque; }
    public boolean isEmPromocao() { return emPromocao; }
    public void setEmPromocao(boolean emPromocao) { this.emPromocao = emPromocao; }
    public Integer getPercentualDesconto() { return percentualDesconto; }
    public void setPercentualDesconto(Integer percentualDesconto) { this.percentualDesconto = percentualDesconto; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public Loja getLoja() { return loja; }
    public void setLoja(Loja loja) { this.loja = loja; }
    
    public BigDecimal getPrecoComDesconto() {
        if (emPromocao && percentualDesconto != null && percentualDesconto > 0) {
            BigDecimal desconto = preco.multiply(BigDecimal.valueOf(percentualDesconto))
                                       .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            return preco.subtract(desconto);
        }
        return preco;
    }
}
