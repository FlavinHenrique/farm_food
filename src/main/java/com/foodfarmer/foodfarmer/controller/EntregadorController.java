package com.foodfarmer.foodfarmer.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.foodfarmer.foodfarmer.model.PapelUsuario;
import com.foodfarmer.foodfarmer.model.Pedido;
import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.service.AutenticacaoService;
import com.foodfarmer.foodfarmer.service.PedidoService;

@Controller
@RequestMapping("/delivery")
public class EntregadorController {
    private final AutenticacaoService autenticacaoService;
    private final PedidoService pedidoService;

    public EntregadorController(AutenticacaoService autenticacaoService, PedidoService pedidoService) {
        this.autenticacaoService = autenticacaoService;
        this.pedidoService = pedidoService;
    }

    @GetMapping("/available")
    public String availableOrders(Model model) {
        Usuario entregador = getEntregadorLogado();
        if (entregador == null) {
            return "redirect:/login?redirect=/delivery/available";
        }

        List<Pedido> pedidosDisponiveis = pedidoService.listarPedidosDisponiveis();

        model.addAttribute("user", entregador);
        model.addAttribute("pedidosDisponiveis", pedidosDisponiveis);
        return "delivery/available";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model,
                            @RequestParam(required = false, defaultValue = "false") boolean somenteHistorico) {
        Usuario entregador = getEntregadorLogado();
        if (entregador == null) {
            return "redirect:/login?redirect=/delivery/dashboard";
        }

        List<Pedido> pedidos = pedidoService.listarPedidosEntregador(entregador, null, null, null, somenteHistorico);

        model.addAttribute("user", entregador);
        model.addAttribute("pedidos", pedidos);
        return "delivery/dashboard";
    }

    @PostMapping("/order/aceitar-disponivel")
    public String aceitarPedidoDisponivel(@RequestParam Long pedidoId, RedirectAttributes redirectAttributes) {
        Usuario entregador = getEntregadorLogado();
        if (entregador == null) {
            return "redirect:/login?redirect=/delivery/available";
        }

        try {
            pedidoService.aceitarPedidoDisponivel(entregador, pedidoId);
            redirectAttributes.addFlashAttribute("message", "Pedido aceito com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/delivery/dashboard";
    }

    @GetMapping("/history")
    public String history(Model model) {
        Usuario entregador = getEntregadorLogado();
        if (entregador == null) {
            return "redirect:/login?redirect=/delivery/history";
        }

        List<Pedido> pedidos = pedidoService.listarPedidosEntregador(entregador, null, null, null, true);

        model.addAttribute("user", entregador);
        model.addAttribute("pedidos", pedidos);
        return "delivery/history";
    }

    @PostMapping("/order/aceitar")
    public String aceitarPedido(@RequestParam Long pedidoId, RedirectAttributes redirectAttributes) {
        Usuario entregador = getEntregadorLogado();
        if (entregador == null) {
            return "redirect:/login?redirect=/delivery/dashboard";
        }

        try {
            pedidoService.atualizarStatusEntrega(entregador, pedidoId,
                    com.foodfarmer.foodfarmer.model.StatusEntrega.ACEITO, null, null, null);
            redirectAttributes.addFlashAttribute("message", "Pedido aceito com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/delivery/dashboard";
    }

    @PostMapping("/order/iniciar-rota")
    public String iniciarRota(@RequestParam Long pedidoId, RedirectAttributes redirectAttributes) {
        Usuario entregador = getEntregadorLogado();
        if (entregador == null) {
            return "redirect:/login?redirect=/delivery/dashboard";
        }

        try {
            pedidoService.atualizarStatusEntrega(entregador, pedidoId,
                    com.foodfarmer.foodfarmer.model.StatusEntrega.EM_ROTA, null, null, null);
            redirectAttributes.addFlashAttribute("message", "Rota iniciada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/delivery/dashboard";
    }

    @PostMapping("/order/chegada-destino")
    public String chegadaDestino(@RequestParam Long pedidoId, RedirectAttributes redirectAttributes) {
        Usuario entregador = getEntregadorLogado();
        if (entregador == null) {
            return "redirect:/login?redirect=/delivery/dashboard";
        }

        try {
            pedidoService.atualizarStatusEntrega(entregador, pedidoId,
                    com.foodfarmer.foodfarmer.model.StatusEntrega.CHEGOU_DESTINO, null, null, null);
            redirectAttributes.addFlashAttribute("message", "Chegada ao destino registrada!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/delivery/dashboard";
    }

    @PostMapping("/order/finalizar")
    public String finalizarEntrega(@RequestParam Long pedidoId,
                                  @RequestParam(required = false) String assinatura,
                                  @RequestParam(required = false) String comprovante,
                                  RedirectAttributes redirectAttributes) {
        Usuario entregador = getEntregadorLogado();
        if (entregador == null) {
            return "redirect:/login?redirect=/delivery/dashboard";
        }

        try {
            pedidoService.atualizarStatusEntrega(entregador, pedidoId,
                    com.foodfarmer.foodfarmer.model.StatusEntrega.ENTREGUE, assinatura, comprovante, null);
            redirectAttributes.addFlashAttribute("message", "Entrega finalizada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/delivery/dashboard";
    }

    @PostMapping("/order/reportar-problema")
    public String reportarProblema(@RequestParam Long pedidoId,
                                   @RequestParam String ocorrencia,
                                   RedirectAttributes redirectAttributes) {
        Usuario entregador = getEntregadorLogado();
        if (entregador == null) {
            return "redirect:/login?redirect=/delivery/dashboard";
        }

        try {
            pedidoService.atualizarStatusEntrega(entregador, pedidoId,
                    com.foodfarmer.foodfarmer.model.StatusEntrega.PROBLEMA, null, null, ocorrencia);
            redirectAttributes.addFlashAttribute("message", "Problema reportado!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/delivery/dashboard";
    }

    @GetMapping("/route/{pedidoId}")
    public String route(@PathVariable Long pedidoId, Model model) {
        Usuario entregador = getEntregadorLogado();
        if (entregador == null) {
            return "redirect:/login?redirect=/delivery/route/" + pedidoId;
        }

        Pedido pedido = pedidoService.getPedidoPorId(pedidoId);
        if (pedido == null || !entregador.equals(pedido.getEntregador())) {
            return "redirect:/delivery/dashboard";
        }

        model.addAttribute("pedido", pedido);
        return "delivery/route";
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

