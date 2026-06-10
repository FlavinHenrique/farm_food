package com.foodfarmer.foodfarmer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.foodfarmer.foodfarmer.model.PapelUsuario;
import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.service.AutenticacaoService;

@Controller
@RequestMapping("/delivery")
public class EntregadorController {
    private final AutenticacaoService autenticacaoService;

    public EntregadorController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Usuario entregador = getEntregadorLogado();
        if (entregador == null) {
            return "redirect:/login?redirect=/delivery/dashboard";
        }
        model.addAttribute("user", entregador);
        return "delivery/dashboard";
    }

    @GetMapping("/history")
    public String history(Model model) {
        Usuario entregador = getEntregadorLogado();
        if (entregador == null) {
            return "redirect:/login?redirect=/delivery/history";
        }
        model.addAttribute("user", entregador);
        return "delivery/history";
    }

    private Usuario getEntregadorLogado() {
        if (!autenticacaoService.estaLogado()) {
            return null;
        }
        Usuario usuario = autenticacaoService.getUsuarioAtual().orElse(null);
        if (usuario == null || usuario.getPapel() != PapelUsuario.ENTREGADOR) {
            return null;
        }
        return usuario;
    }
}

