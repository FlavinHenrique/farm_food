package com.foodfarmer.foodfarmer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.service.UsuarioService;

@Controller
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new Usuario());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") Usuario user, Model model) {
        try {
            usuarioService.registrarCliente(user);
            return "redirect:/?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", "Este e-mail já está cadastrado.");
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
            usuarioService.registrarProdutor(user);
            return "redirect:/?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", "Este e-mail ou CNPJ já está cadastrado.");
            return "register-producer";
        }
    }
}
