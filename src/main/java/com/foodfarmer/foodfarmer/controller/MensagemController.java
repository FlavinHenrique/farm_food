package com.foodfarmer.foodfarmer.controller;

import com.foodfarmer.foodfarmer.model.Loja;
import com.foodfarmer.foodfarmer.model.Mensagem;
import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.service.AutenticacaoService;
import com.foodfarmer.foodfarmer.service.LojaService;
import com.foodfarmer.foodfarmer.service.MensagemService;
import com.foodfarmer.foodfarmer.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MensagemController {
    private final MensagemService mensagemService;
    private final AutenticacaoService autenticacaoService;
    private final LojaService lojaService;
    private final UsuarioService usuarioService;

    public MensagemController(MensagemService mensagemService, AutenticacaoService autenticacaoService, 
                              LojaService lojaService, UsuarioService usuarioService) {
        this.mensagemService = mensagemService;
        this.autenticacaoService = autenticacaoService;
        this.lojaService = lojaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/producer/messages")
    public String producerMessages(@RequestParam(required = false) Long contactId, Model model) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        Usuario produtor = autenticacaoService.getUsuarioAtual().get();
        
        List<Mensagem> todasMensagens = mensagemService.getMensagensRecebidas(produtor);
        todasMensagens.addAll(mensagemService.getMensagensEnviadas(produtor));
        
        // Agrupar contatos ÃƒÆ’Ã‚Âºnicos
        List<Usuario> contatos = todasMensagens.stream()
            .map(m -> m.getRemetente().getId().equals(produtor.getId()) ? m.getDestinatario() : m.getRemetente())
            .distinct()
            .toList();

        model.addAttribute("contatos", contatos);
        model.addAttribute("produtor", produtor);

        if (contactId != null) {
            Usuario contato = usuarioService.findById(contactId).orElseThrow();
            model.addAttribute("contatoAtivo", contato);
            model.addAttribute("conversa", mensagemService.getConversa(produtor, contato));
        }
        
        return "producer/messages";
    }

    @GetMapping("/customer/messages")
    public String customerMessages(@RequestParam(required = false) Long contactId, Model model) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        Usuario cliente = autenticacaoService.getUsuarioAtual().get();
        
        List<Mensagem> todasMensagens = new java.util.ArrayList<>();
        todasMensagens.addAll(mensagemService.getMensagensRecebidas(cliente));
        todasMensagens.addAll(mensagemService.getMensagensEnviadas(cliente));

        // Ordenar por data de envio decrescente (mais recente primeiro)
        todasMensagens.sort((m1, m2) -> m2.getDataEnvio().compareTo(m1.getDataEnvio()));

        // Agrupar contatos Ãºnicos
        List<Usuario> contatos = todasMensagens.stream()
            .map(m -> m.getRemetente().getId().equals(cliente.getId()) ? m.getDestinatario() : m.getRemetente())
            .distinct()
            .toList();

        model.addAttribute("contatos", contatos);
        model.addAttribute("mensagens", todasMensagens);
        model.addAttribute("usuario", cliente);

        if (contactId != null) {
            Usuario contato = usuarioService.findById(contactId).orElseThrow();
            model.addAttribute("contatoAtivo", contato);
            model.addAttribute("conversa", mensagemService.getConversa(cliente, contato));
        }
        
        return "customer/messages";
    }

    @PostMapping("/messages/send")
    public String sendMessage(@RequestParam Long lojaId, @RequestParam String conteudo, @RequestParam(required = false) String origin) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        Usuario remetente = autenticacaoService.getUsuarioAtual().get();
        
        Loja loja = lojaService.getLojaPorId(lojaId).orElseThrow();
        Usuario destinatario = loja.getDono();
        
        Mensagem mensagem = new Mensagem(remetente, destinatario, loja, conteudo);
        mensagemService.enviarMensagem(mensagem);
        
        if ("chat".equals(origin)) {
            return "redirect:/customer/messages?contactId=" + destinatario.getId();
        }
        return "redirect:/customer/messages?sent=true";
    }

    @PostMapping("/messages/reply")
    public String replyMessage(@RequestParam Long destinatarioId, 
                              @RequestParam(required = false) Long lojaId, 
                              @RequestParam String conteudo,
                              @RequestParam String role) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        Usuario remetente = autenticacaoService.getUsuarioAtual().get();
        Usuario destinatario = usuarioService.findById(destinatarioId).orElseThrow();
        
        Loja loja = null;
        if (lojaId != null) {
            loja = lojaService.getLojaPorId(lojaId).orElse(null);
        }
        
        Mensagem mensagem = new Mensagem(remetente, destinatario, loja, conteudo);
        mensagemService.enviarMensagem(mensagem);
        
        if ("PRODUTOR".equals(role)) {
            return "redirect:/producer/messages?contactId=" + destinatarioId;
        }
        return "redirect:/customer/messages?contactId=" + destinatarioId;
    }
}
