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
    private String nomeEmpresa;
    private String cpf;
    private String cnpj;
    private String telefone;
    private String urlFotoPerfil;
    private String urlFotoCapa;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String senha;
    
    @Enumerated(EnumType.STRING)
    private PapelUsuario papel;
    
    private boolean contaAtiva = true;

    @OneToMany(mappedBy = "dono")
    private List<Loja> lojas;

    public Usuario(Long id, String nome, String nomeEmpresa, String cpf, String cnpj, String telefone, 
                   String urlFotoPerfil, String urlFotoCapa, String email, String senha, PapelUsuario papel, 
                   boolean contaAtiva, List<Loja> lojas) {
        this.id = id;
        this.nome = nome;
        this.nomeEmpresa = nomeEmpresa;
        this.cpf = cpf;
        this.cnpj = cnpj;
        this.telefone = telefone;
        this.urlFotoPerfil = urlFotoPerfil;
        this.urlFotoCapa = urlFotoCapa;
        this.email = email;
        this.senha = senha;
        this.papel = papel;
        this.contaAtiva = contaAtiva;
        this.lojas = lojas;
    }

    public Usuario() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getNomeEmpresa() { return nomeEmpresa; }
    public void setNomeEmpresa(String nomeEmpresa) { this.nomeEmpresa = nomeEmpresa; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getUrlFotoPerfil() { return urlFotoPerfil; }
    public void setUrlFotoPerfil(String urlFotoPerfil) { this.urlFotoPerfil = urlFotoPerfil; }
    public String getUrlFotoCapa() { return urlFotoCapa; }
    public void setUrlFotoCapa(String urlFotoCapa) { this.urlFotoCapa = urlFotoCapa; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public PapelUsuario getPapel() { return papel; }
    public void setPapel(PapelUsuario papel) { this.papel = papel; }
    public boolean isContaAtiva() { return contaAtiva; }
    public void setContaAtiva(boolean contaAtiva) { this.contaAtiva = contaAtiva; }
    public List<Loja> getLojas() { return lojas; }
    public void setLojas(List<Loja> lojas) { this.lojas = lojas; }
}
