package com.foodfarmer.foodfarmer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodfarmer.foodfarmer.model.Loja;
import com.foodfarmer.foodfarmer.model.PapelUsuario;
import com.foodfarmer.foodfarmer.model.Produto;
import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.service.AutenticacaoService;
import com.foodfarmer.foodfarmer.service.ClienteService;
import com.foodfarmer.foodfarmer.service.LojaService;
import com.foodfarmer.foodfarmer.service.ProdutoService;

@Controller
public class HomeController {
    private final ProdutoService produtoService;
    private final AutenticacaoService autenticacaoService;
    private final ClienteService clienteService;

    private final LojaService lojaService;

    public HomeController(ProdutoService produtoService, AutenticacaoService autenticacaoService, 
                          ClienteService clienteService, LojaService lojaService) {
        this.produtoService = produtoService;
        this.autenticacaoService = autenticacaoService;
        this.clienteService = clienteService;
        this.lojaService = lojaService;
    }

    @GetMapping("/")
    public String index(@RequestParam(required = false) Long categoriaId, 
                        @RequestParam(required = false) Boolean emOferta,
                        Model model) {
        model.addAttribute("isLoggedIn", autenticacaoService.estaLogado());
        model.addAttribute("isProducer", autenticacaoService.estaLogado() && 
            autenticacaoService.getUsuarioAtual().get().getPapel() == PapelUsuario.PRODUTOR);
        
        model.addAttribute("categories", produtoService.getTodasCategorias());
        
        // Filtrar produtos em promoção que estejam incompletos
        java.util.List<Produto> saleProducts = produtoService.getProdutosEmPromocao().stream()
            .filter(p -> p.getNome() != null && p.getPreco() != null && p.getUnidade() != null)
            .toList();
        model.addAttribute("saleProducts", saleProducts);
        
        model.addAttribute("stores", produtoService.getTodasLojas());
        
        // Lógica de filtragem de produtos (removendo produtos com dados incompletos/null)
        java.util.List<Produto> products;
        if (Boolean.TRUE.equals(emOferta)) {
            products = produtoService.getProdutosEmPromocao();
            model.addAttribute("selectedCategory", "oferta");
        } else if (categoriaId != null) {
            products = produtoService.getProdutosPorCategoria(categoriaId);
            model.addAttribute("selectedCategoryId", categoriaId);
        } else {
            products = produtoService.getTodosProdutos();
        }

        // Filtrar produtos que tenham campos essenciais nulos (nome, preço ou unidade)
        java.util.List<Produto> validProducts = products.stream()
            .filter(p -> p.getNome() != null && p.getPreco() != null && p.getUnidade() != null)
            .toList();

        model.addAttribute("products", validProducts);
        
        return "index";
    }

    @GetMapping("/produtos")
    public String produtos(@RequestParam(required = false) Long categoriaId,
                           @RequestParam(required = false) String search,
                           Model model) {
        model.addAttribute("isLoggedIn", autenticacaoService.estaLogado());
        model.addAttribute("isProducer", autenticacaoService.estaLogado() && 
            autenticacaoService.getUsuarioAtual().get().getPapel() == PapelUsuario.PRODUTOR);
        
        model.addAttribute("categories", produtoService.getTodasCategorias());
        
        java.util.List<Produto> products;
        if (categoriaId != null) {
            products = produtoService.getProdutosPorCategoria(categoriaId);
            model.addAttribute("selectedCategoryId", categoriaId);
        } else {
            products = produtoService.getTodosProdutos();
        }

        // Filtrar produtos incompletos
        java.util.List<Produto> validProducts = products.stream()
            .filter(p -> p.getNome() != null && p.getPreco() != null && p.getUnidade() != null)
            .toList();
        
        model.addAttribute("products", validProducts);
        
        return "produtos";
    }

    @GetMapping("/produtores")
    public String produtores(Model model) {
        model.addAttribute("isLoggedIn", autenticacaoService.estaLogado());
        model.addAttribute("isProducer", autenticacaoService.estaLogado() && 
            autenticacaoService.getUsuarioAtual().get().getPapel() == PapelUsuario.PRODUTOR);
        
        model.addAttribute("stores", produtoService.getTodasLojas());
        
        return "produtores";
    }

    @GetMapping("/produto/{id}")
    public String produtoDetalhe(@PathVariable Long id, Model model) {
        Produto produto = produtoService.getProdutoPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
        
        model.addAttribute("produto", produto);
        model.addAttribute("outrasLojas", produtoService.getProdutosMesmoNome(produto.getNome()));
        model.addAttribute("isLoggedIn", autenticacaoService.estaLogado());
        
        return "product-detail";
    }

    @GetMapping("/loja/{id}")
    public String lojaDetalhe(@PathVariable Long id, Model model) {
        Loja loja = lojaService.getLojaPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada: " + id));
        
        model.addAttribute("loja", loja);
        
        // Filtrar produtos da loja que estejam incompletos
        java.util.List<Produto> products = produtoService.getProdutosPorLoja(id).stream()
            .filter(p -> p.getNome() != null && p.getPreco() != null && p.getUnidade() != null)
            .toList();
        model.addAttribute("products", products);
        
        model.addAttribute("isLoggedIn", autenticacaoService.estaLogado());
        
        return "store-detail";
    }

    @GetMapping("/cart")
    public String cart(Model model) {
        model.addAttribute("isLoggedIn", autenticacaoService.estaLogado());
        return "cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model) {
        if (!autenticacaoService.estaLogado()) {
            return "redirect:/login?redirect=/checkout";
        }
        
        Usuario usuario = autenticacaoService.getUsuarioAtual().get();
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("usuario", usuario);
        model.addAttribute("addresses", clienteService.getEnderecosPorUsuario(usuario));
        
        return "checkout";
    }
}
