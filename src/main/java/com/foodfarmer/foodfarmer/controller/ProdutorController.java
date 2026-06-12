package com.foodfarmer.foodfarmer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodfarmer.foodfarmer.model.Categoria;
import com.foodfarmer.foodfarmer.model.Loja;
import com.foodfarmer.foodfarmer.model.Pedido;
import com.foodfarmer.foodfarmer.model.Produto;
import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.service.AutenticacaoService;
import com.foodfarmer.foodfarmer.service.LojaService;
import com.foodfarmer.foodfarmer.service.PedidoService;
import com.foodfarmer.foodfarmer.service.ProdutoService;
import com.foodfarmer.foodfarmer.service.UsuarioService;

@Controller
@RequestMapping("/producer")
public class ProdutorController {

    private final LojaService lojaService;
    private final ProdutoService produtoService;
    private final AutenticacaoService autenticacaoService;
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;

    public ProdutorController(LojaService lojaService, ProdutoService produtoService, 
                            AutenticacaoService autenticacaoService, PedidoService pedidoService, UsuarioService usuarioService) {
        this.lojaService = lojaService;
        this.produtoService = produtoService;
        this.autenticacaoService = autenticacaoService;
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        Usuario produtor = autenticacaoService.getUsuarioAtual().get();
        java.util.List<Loja> lojas = lojaService.getLojasPorDono(produtor);
        java.util.List<Pedido> vendas = pedidoService.getPedidosPorLojas(lojas);
        
        model.addAttribute("produtor", produtor);
        model.addAttribute("stores", lojas);
        model.addAttribute("vendas", vendas);
        model.addAttribute("totalFaturamento", pedidoService.calcularFaturamentoTotal(vendas));
        model.addAttribute("totalVendas", vendas.size());
        
        // Atributo para o modal de categoria
        model.addAttribute("newCategory", new Categoria());
        
        return "producer/dashboard";
    }

    @GetMapping("/vendas")
    public String sales(Model model) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        Usuario produtor = autenticacaoService.getUsuarioAtual().get();
        java.util.List<Loja> lojas = lojaService.getLojasPorDono(produtor);
        java.util.List<Pedido> vendas = pedidoService.getPedidosPorLojas(lojas);
        
        model.addAttribute("produtor", produtor);
        model.addAttribute("vendas", vendas);
        return "producer/vendas";
    }

    @GetMapping("/stores")
    public String stores(Model model) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        Usuario produtor = autenticacaoService.getUsuarioAtual().get();
        model.addAttribute("produtor", produtor);
        model.addAttribute("stores", lojaService.getLojasPorDono(produtor));
        return "producer/stores";
    }

    @GetMapping("/products")
    public String products(Model model) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        Usuario produtor = autenticacaoService.getUsuarioAtual().get();
        model.addAttribute("produtor", produtor);
        
        java.util.List<Loja> lojas = lojaService.getLojasPorDono(produtor);
        java.util.List<Produto> produtos = new java.util.ArrayList<>();
        for (Loja loja : lojas) {
            produtos.addAll(produtoService.getProdutosPorLojaIncluindoInativos(loja.getId()));
        }
        
        model.addAttribute("stores", lojas);
        model.addAttribute("products", produtos);
        return "producer/products";
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        Usuario produtor = autenticacaoService.getUsuarioAtual().get();
        model.addAttribute("produtor", produtor);
        return "producer/profile";
    }

    @PostMapping("/profile/save")
    public String saveProfile(@ModelAttribute Usuario produtor, @RequestParam(required = false) String newPassword) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        Usuario currentUser = autenticacaoService.getUsuarioAtual().get();
        
        // Atualizar os campos
        currentUser.setNome(produtor.getNome());
        currentUser.setNomeEmpresa(produtor.getNomeEmpresa());
        currentUser.setCpf(produtor.getCpf());
        currentUser.setCnpj(produtor.getCnpj());
        currentUser.setTelefone(produtor.getTelefone());
        currentUser.setUrlFotoPerfil(produtor.getUrlFotoPerfil());
        currentUser.setUrlFotoCapa(produtor.getUrlFotoCapa());
        currentUser.setEmail(produtor.getEmail());
        
        // Atualizar senha se fornecida
        if (newPassword != null && !newPassword.isBlank()) {
            currentUser.setSenha(newPassword);
        }
        
        usuarioService.atualizar(currentUser);
        return "redirect:/producer/profile";
    }

    @GetMapping("/store/new")
    public String showStoreForm(Model model) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        model.addAttribute("store", new Loja());
        return "producer/store-form";
    }

    @GetMapping("/store/edit/{id}")
    public String showEditStoreForm(@PathVariable("id") Long id, Model model) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        Loja loja = lojaService.getLojaPorId(id).orElseThrow(() -> new IllegalArgumentException("Loja inválida:" + id));
        model.addAttribute("store", loja);
        return "producer/store-form";
    }

    @GetMapping("/store/delete/{id}")
    public String deleteStore(@PathVariable("id") Long id) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        lojaService.excluirLoja(id);
        return "redirect:/producer/stores";
    }

    @PostMapping("/store/save")
    public String saveStore(@ModelAttribute Loja loja) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        loja.setDono(autenticacaoService.getUsuarioAtual().get());
        lojaService.salvarLoja(loja);
        return "redirect:/producer/stores";
    }

    @GetMapping("/product/new")
    public String showProductForm(@RequestParam("storeId") Long storeId, Model model) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        Produto produto = new Produto();
        Loja loja = lojaService.getLojaPorId(storeId).orElseThrow();
        produto.setLoja(loja);
        
        model.addAttribute("product", produto);
        model.addAttribute("categories", produtoService.getTodasCategorias());
        model.addAttribute("newCategory", new Categoria());
        model.addAttribute("storeId", storeId);
        
        return "producer/product-form";
    }

    @PostMapping("/product/save")
    public String saveProduct(@ModelAttribute Produto produto, @RequestParam(required = false) Long lojaId) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        
        // Se o objeto loja no produto estiver nulo ou sem ID, tenta recuperar pelo parâmetro
        if (produto.getLoja() == null || produto.getLoja().getId() == null) {
            if (lojaId != null) {
                Loja loja = lojaService.getLojaPorId(lojaId).orElseThrow();
                produto.setLoja(loja);
            }
        }
        
        produtoService.salvarProduto(produto);
        return "redirect:/producer/products";
    }

    @GetMapping("/product/edit/{id}")
    public String showEditProductForm(@PathVariable("id") Long id, Model model) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        Produto produto = produtoService.getProdutoPorId(id).orElseThrow(() -> new IllegalArgumentException("Produto inválido:" + id));
        
        model.addAttribute("product", produto);
        model.addAttribute("categories", produtoService.getTodasCategorias());
        model.addAttribute("newCategory", new Categoria());
        model.addAttribute("storeId", produto.getLoja().getId());
        
        return "producer/product-form";
    }

    @GetMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        produtoService.excluirProduto(id);
        return "redirect:/producer/products";
    }

    @GetMapping("/product/activate/{id}")
    public String activateProduct(@PathVariable("id") Long id) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        produtoService.reativarProduto(id);
        return "redirect:/producer/products";
    }

    @PostMapping("/category/save")
    public String saveCategory(@ModelAttribute Categoria category, @RequestParam(required = false) Long storeId) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        produtoService.salvarCategoria(category);
        
        if (storeId != null) {
            return "redirect:/producer/product/new?storeId=" + storeId;
        }
        return "redirect:/producer/products";
    }
}
