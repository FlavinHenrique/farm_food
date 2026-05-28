package com.foodfarmer.foodfarmer.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.foodfarmer.foodfarmer.model.Endereco;
import com.foodfarmer.foodfarmer.model.MetodoPagamento;
import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.service.AutenticacaoService;
import com.foodfarmer.foodfarmer.service.ClienteService;
import com.foodfarmer.foodfarmer.service.UsuarioService;

@Controller
@RequestMapping("/customer")
public class ClienteController {

    private final ClienteService clienteService;
    private final AutenticacaoService autenticacaoService;
    private final UsuarioService usuarioService;

    public ClienteController(ClienteService clienteService, AutenticacaoService autenticacaoService, UsuarioService usuarioService) {
        this.clienteService = clienteService;
        this.autenticacaoService = autenticacaoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        if (!autenticacaoService.estaLogado()) {
            return "redirect:/login";
        }
        
        Usuario customer = autenticacaoService.getUsuarioAtual().get();
        model.addAttribute("usuario", customer);
        model.addAttribute("addresses", clienteService.getEnderecosPorUsuario(customer)); 
        model.addAttribute("payments", clienteService.getMetodosPagamentoPorUsuario(customer));
        model.addAttribute("favorites", new ArrayList<>());
        
        // Objetos para os formulários de cadastro
        model.addAttribute("newAddress", new Endereco());
        model.addAttribute("newPayment", new MetodoPagamento());
        
        return "customer/profile";
    }

    @GetMapping("/addresses")
    public String addresses(Model model) {
        if (!autenticacaoService.estaLogado()) {
            return "redirect:/login";
        }
        Usuario customer = autenticacaoService.getUsuarioAtual().get();
        model.addAttribute("usuario", customer);
        model.addAttribute("addresses", clienteService.getEnderecosPorUsuario(customer));
        model.addAttribute("newAddress", new Endereco());
        return "customer/addresses";
    }

    @GetMapping("/payments")
    public String payments(Model model) {
        if (!autenticacaoService.estaLogado()) {
            return "redirect:/login";
        }
        Usuario customer = autenticacaoService.getUsuarioAtual().get();
        model.addAttribute("usuario", customer);
        model.addAttribute("payments", clienteService.getMetodosPagamentoPorUsuario(customer));
        model.addAttribute("newPayment", new MetodoPagamento());
        return "customer/payments";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute Usuario usuario) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        
        Usuario atual = autenticacaoService.getUsuarioAtual().get();
        atual.setNome(usuario.getNome());
        atual.setEmail(usuario.getEmail());
        // Não atualizamos a senha aqui por segurança, ou teríamos um campo específico
        
        usuarioService.atualizar(atual);
        autenticacaoService.login(atual); // Atualiza na sessão
        
        return "redirect:/customer/profile?success=true";
    }

    @PostMapping("/address/save")
    public String saveAddress(@ModelAttribute Endereco address) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        address.setUsuario(autenticacaoService.getUsuarioAtual().get());
        clienteService.salvarEndereco(address);
        return "redirect:/customer/addresses?success=true";
    }

    @GetMapping("/address/delete/{id}")
    public String deleteAddress(@PathVariable("id") Long id) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        clienteService.excluirEndereco(id);
        return "redirect:/customer/addresses?deleted=true";
    }

    @PostMapping("/payment/save")
    public String savePayment(@ModelAttribute MetodoPagamento paymentMethod) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        if (!clienteService.validarNumeroCartao(paymentMethod.getNumeroCartao())) {
            return "redirect:/customer/payments?error=invalid_card";
        }
        paymentMethod.setUsuario(autenticacaoService.getUsuarioAtual().get());
        clienteService.salvarMetodoPagamento(paymentMethod);
        return "redirect:/customer/payments?success=true";
    }

    @GetMapping("/payment/delete/{id}")
    public String deletePayment(@PathVariable("id") Long id) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        clienteService.excluirMetodoPagamento(id);
        return "redirect:/customer/payments?deleted=true";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/logout";
    }
}
