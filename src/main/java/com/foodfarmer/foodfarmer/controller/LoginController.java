package com.foodfarmer.foodfarmer.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodfarmer.foodfarmer.model.PapelUsuario;
import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.service.AutenticacaoService;
import com.foodfarmer.foodfarmer.service.UsuarioService;

@Controller
public class LoginController {

    private final UsuarioService usuarioService;
    private final AutenticacaoService autenticacaoService;

    public LoginController(UsuarioService usuarioService, AutenticacaoService autenticacaoService) {
        this.usuarioService = usuarioService;
        this.autenticacaoService = autenticacaoService;
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String redirect, Model model) {
        if (autenticacaoService.estaLogado()) {
            return "redirect:" + resolveDefaultRedirect(autenticacaoService.getUsuarioAtual().orElse(null), redirect);
        }
        model.addAttribute("redirect", redirect);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, 
                        @RequestParam String password, 
                        @RequestParam(required = false) String redirect,
                        Model model) {
        Optional<Usuario> userOpt = usuarioService.findByEmail(email);
        
        
        if (userOpt.isPresent() && userOpt.get().getSenha().equals(password)) {
            autenticacaoService.login(userOpt.get());
            return "redirect:" + resolveDefaultRedirect(userOpt.get(), redirect);
        }
        
        model.addAttribute("error", "E-mail ou senha inválidos");
        model.addAttribute("redirect", redirect);
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        autenticacaoService.logout();
        return "redirect:/?logout=true";
    }

    private String resolveDefaultRedirect(Usuario usuario, String redirect) {
        if (redirect != null && !redirect.isBlank()) {
            return redirect;
        }
        if (usuario == null || usuario.getPapel() == null) {
            return "/";
        }
        if (usuario.getPapel() == PapelUsuario.PRODUTOR) {
            return "/producer/dashboard";
        }
        if (usuario.getPapel() == PapelUsuario.ENTREGADOR) {
            return "/delivery/dashboard";
        }
        return "/customer/profile";
    }
}
