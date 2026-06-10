package com.foodfarmer.foodfarmer.service;

import com.foodfarmer.foodfarmer.model.Mensagem;
import com.foodfarmer.foodfarmer.model.Usuario;
import com.foodfarmer.foodfarmer.repository.MensagemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MensagemService {
    private final MensagemRepository mensagemRepository;

    public MensagemService(MensagemRepository mensagemRepository) {
        this.mensagemRepository = mensagemRepository;
    }

    public Mensagem enviarMensagem(Mensagem mensagem) {
        return mensagemRepository.save(mensagem);
    }

    public List<Mensagem> getMensagensRecebidas(Usuario usuario) {
        return mensagemRepository.findByDestinatarioOrderByDataEnvioDesc(usuario);
    }

    public List<Mensagem> getMensagensEnviadas(Usuario usuario) {
        return mensagemRepository.findByRemetenteOrderByDataEnvioDesc(usuario);
    }

    public List<Mensagem> getConversa(Usuario u1, Usuario u2) {
        return mensagemRepository.findByRemetenteAndDestinatarioOrRemetenteAndDestinatarioOrderByDataEnvioAsc(
            u1, u2, u2, u1
        );
    }

    public void marcarComoLida(Long mensagemId) {
        mensagemRepository.findById(mensagemId).ifPresent(m -> {
            m.setLida(true);
            mensagemRepository.save(m);
        });
    }
}
