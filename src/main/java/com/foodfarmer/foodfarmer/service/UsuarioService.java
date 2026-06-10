package com.foodfarmer.foodfarmer.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.model.PapelUsuario;
import com.foodfarmer.foodfarmer.repository.UsuarioRepository;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario registrarCliente(Usuario usuario) {
        // Em um sistema real, criptografe a senha antes de salvar:
        // usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setPapel(PapelUsuario.CLIENTE);
        return usuarioRepository.save(usuario);
    }

    public Usuario registrarProdutor(Usuario usuario) {
        // Em um sistema real, criptografe a senha antes de salvar:
        // usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setPapel(PapelUsuario.PRODUTOR);
        return usuarioRepository.save(usuario);
    }

    public Usuario registrarEntregador(Usuario usuario) {
        usuario.setPapel(PapelUsuario.ENTREGADOR);
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
}
