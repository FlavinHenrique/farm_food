package com.foodfarmer.foodfarmer.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "lojas")
public class Loja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nome;
    private String descricao;
    private String urlImagem;
    
    @ManyToOne
    @JoinColumn(name = "dono_id")
    private Usuario dono;
    
    @OneToMany(mappedBy = "loja", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Produto> produtos;

    public Loja(Long id, String nome, String descricao, String urlImagem, Usuario dono, List<Produto> produtos) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.urlImagem = urlImagem;
        this.dono = dono;
        this.produtos = produtos;
    }

    public Loja() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getUrlImagem() { return urlImagem; }
    public void setUrlImagem(String urlImagem) { this.urlImagem = urlImagem; }
    public Usuario getDono() { return dono; }
    public void setDono(Usuario dono) { this.dono = dono; }
    public List<Produto> getProdutos() { return produtos; }
    public void setProdutos(List<Produto> produtos) { this.produtos = produtos; }
}
