package com.foodfarmer.foodfarmer.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nome;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String senha;
    private String cnpj; 
    
    @Enumerated(EnumType.STRING)
    private PapelUsuario papel;

    @OneToMany(mappedBy = "dono")
    private List<Loja> lojas;

    public Usuario(Long id, String nome, String email, String senha, String cnpj, PapelUsuario papel, List<Loja> lojas) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cnpj = cnpj;
        this.papel = papel;
        this.lojas = lojas;
    }

    public Usuario() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public PapelUsuario getPapel() { return papel; }
    public void setPapel(PapelUsuario papel) { this.papel = papel; }
    public List<Loja> getLojas() { return lojas; }
    public void setLojas(List<Loja> lojas) { this.lojas = lojas; }
}
