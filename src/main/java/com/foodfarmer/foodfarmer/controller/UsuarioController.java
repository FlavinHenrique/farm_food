package com.foodfarmer.foodfarmer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.service.AutenticacaoService;
import com.foodfarmer.foodfarmer.service.UsuarioService;

@Controller
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final AutenticacaoService autenticacaoService;

    public UsuarioController(UsuarioService usuarioService, AutenticacaoService autenticacaoService) {
        this.usuarioService = usuarioService;
        this.autenticacaoService = autenticacaoService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(@RequestParam(required = false) String redirect, Model model) {
        model.addAttribute("user", new Usuario());
        model.addAttribute("redirect", redirect);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") Usuario user, 
                               @RequestParam(required = false) String redirect,
                               Model model) {
        try {
            Usuario salvo = usuarioService.registrarCliente(user);
            autenticacaoService.login(salvo);
            
            if (redirect != null && !redirect.isEmpty()) {
                return "redirect:" + redirect;
            }
            return "redirect:/?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", "Este e-mail jÃ¡ estÃ¡ cadastrado.");
            model.addAttribute("redirect", redirect);
            return "register";
        }
    }

    @GetMapping("/register/producer")
    public String showProducerRegistrationForm(Model model) {
        model.addAttribute("user", new Usuario());
        return "register-producer";
    }

    @PostMapping("/register/producer")
    public String registerProducer(@ModelAttribute("user") Usuario user, Model model) {
        try {
            Usuario salvo = usuarioService.registrarProdutor(user);
            autenticacaoService.login(salvo);
            return "redirect:/producer/dashboard?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", "Este e-mail ou CNPJ jÃ¡ estÃ¡ cadastrado.");
            return "register-producer";
        }
    }

    @GetMapping("/register/delivery")
    public String showDeliveryRegistrationForm(Model model) {
        model.addAttribute("user", new Usuario());
        return "register-delivery";
    }

    @PostMapping("/register/delivery")
    public String registerDelivery(@ModelAttribute("user") Usuario user, Model model) {
        try {
            Usuario salvo = usuarioService.registrarEntregador(user);
            autenticacaoService.login(salvo);
            return "redirect:/delivery/dashboard?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", "Este e-mail jÃ¡ estÃ¡ cadastrado.");
            return "register-delivery";
        }
    }
}
