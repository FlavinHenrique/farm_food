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
import com.foodfarmer.foodfarmer.model.Produto;
import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.service.AutenticacaoService;
import com.foodfarmer.foodfarmer.service.LojaService;
import com.foodfarmer.foodfarmer.service.ProdutoService;

@Controller
@RequestMapping("/producer")
public class ProdutorController {

    private final LojaService lojaService;
    private final ProdutoService produtoService;
    private final AutenticacaoService autenticacaoService;

    public ProdutorController(LojaService lojaService, ProdutoService produtoService, AutenticacaoService autenticacaoService) {
        this.lojaService = lojaService;
        this.produtoService = produtoService;
        this.autenticacaoService = autenticacaoService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        if (!autenticacaoService.estaLogado()) return "redirect:/login";
        Usuario produtor = autenticacaoService.getUsuarioAtual().get();
        model.addAttribute("produtor", produtor);
        model.addAttribute("stores", lojaService.getLojasPorDono(produtor));
        
        // Atributo para o modal de categoria
        model.addAttribute("newCategory", new Categoria());
        
        return "producer/dashboard";
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
            produtos.addAll(loja.getProdutos());
        }
        
        model.addAttribute("stores", lojas);
        model.addAttribute("products", produtos);
        return "producer/products";
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
