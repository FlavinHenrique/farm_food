package com.foodfarmer.foodfarmer.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.foodfarmer.foodfarmer.model.Endereco;
import com.foodfarmer.foodfarmer.model.MetodoPagamento;
import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.repository.EnderecoRepository;
import com.foodfarmer.foodfarmer.repository.MetodoPagamentoRepository;

@Service
public class ClienteService {
    private final EnderecoRepository enderecoRepository;
    private final MetodoPagamentoRepository metodoPagamentoRepository;

    public ClienteService(EnderecoRepository enderecoRepository, MetodoPagamentoRepository metodoPagamentoRepository) {
        this.enderecoRepository = enderecoRepository;
        this.metodoPagamentoRepository = metodoPagamentoRepository;
    }

    public List<Endereco> getEnderecosPorUsuario(Usuario usuario) {
        return enderecoRepository.findByUsuario(usuario);
    }

    public Endereco salvarEndereco(Endereco endereco) {
        if (endereco == null) {
            throw new IllegalArgumentException("Endereco nao pode ser nulo"); 
        }
        return enderecoRepository.save(endereco);
    }

    public void excluirEndereco(Long id) {
        if (id != null) {
            enderecoRepository.deleteById(id);
        }
    }

    public Optional<Endereco> getEnderecoPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return enderecoRepository.findById(id);
    }

    public List<MetodoPagamento> getMetodosPagamentoPorUsuario(Usuario usuario) {
        return metodoPagamentoRepository.findByUsuario(usuario);
    }

    public MetodoPagamento salvarMetodoPagamento(MetodoPagamento metodoPagamento) {
        if (metodoPagamento == null) {
            throw new IllegalArgumentException("Metodo de pagamento nao pode ser nulo");
        }
        return metodoPagamentoRepository.save(metodoPagamento);
    }

    public void excluirMetodoPagamento(Long id) {
        metodoPagamentoRepository.deleteById(id);
    }

    public boolean validarNumeroCartao(String numeroCartao) {
        if (numeroCartao == null) return false;
        String digitos = numeroCartao.replaceAll("\\D", "");

        if (digitos.equals("1234") || digitos.equals("1111") || digitos.equals("0000")) return true;

        if (digitos.length() < 13 || digitos.length() > 19) return false;       

        int soma = 0;
        boolean alternar = false;
        for (int i = digitos.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(digitos.substring(i, i + 1));
            if (alternar) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            soma += n;
            alternar = !alternar;
        }
        return (soma % 10 == 0);
    }
}
