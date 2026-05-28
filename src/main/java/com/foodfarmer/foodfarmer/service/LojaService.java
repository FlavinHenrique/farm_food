package com.foodfarmer.foodfarmer.service;

import com.foodfarmer.foodfarmer.model.Loja;
import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.repository.LojaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LojaService {
    private final LojaRepository lojaRepository;

    public LojaService(LojaRepository lojaRepository) {
        this.lojaRepository = lojaRepository;
    }

    public List<Loja> getLojasPorDono(Usuario dono) {
        return lojaRepository.findAll().stream()
                .filter(s -> s.getDono() != null && s.getDono().getId().equals(dono.getId()))
                .toList();
    }

    public Loja salvarLoja(Loja loja) {
        if (loja == null) {
            throw new IllegalArgumentException("Loja não pode ser nula");
        }
        return lojaRepository.save(loja);
    }

    public Optional<Loja> getLojaPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return lojaRepository.findById(id);
    }

    public void excluirLoja(Long id) {
        if (id != null) {
            lojaRepository.deleteById(id);
        }
    }
}
