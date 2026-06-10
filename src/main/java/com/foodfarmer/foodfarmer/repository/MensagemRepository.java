package com.foodfarmer.foodfarmer.repository;

import com.foodfarmer.foodfarmer.model.Mensagem;
import com.foodfarmer.foodfarmer.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
    List<Mensagem> findByDestinatarioOrderByDataEnvioDesc(Usuario destinatario);
    List<Mensagem> findByRemetenteOrderByDataEnvioDesc(Usuario remetente);
    List<Mensagem> findByRemetenteAndDestinatarioOrRemetenteAndDestinatarioOrderByDataEnvioAsc(
        Usuario u1, Usuario u2, Usuario u3, Usuario u4
    );
}
