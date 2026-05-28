package com.foodfarmer.foodfarmer.service;

import com.foodfarmer.foodfarmer.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Optional;

@Service
@SessionScope
public class AutenticacaoService {
    private Usuario usuarioAtual;

    public void login(Usuario usuario) {
        this.usuarioAtual = usuario;
    }

    public void logout() {
        this.usuarioAtual = null;
    }

    public boolean estaLogado() {
        return usuarioAtual != null;
    }

    public Optional<Usuario> getUsuarioAtual() {
        return Optional.ofNullable(usuarioAtual);
    }
}
