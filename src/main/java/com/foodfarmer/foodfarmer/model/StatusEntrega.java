package com.foodfarmer.foodfarmer.model;

public enum StatusEntrega {
    PENDENTE,
    ACEITO,
    EM_ROTA,
    CHEGOU_DESTINO,
    ENTREGUE,
    PROBLEMA,
    CANCELADO;

    public boolean isConcluido() {
        return this == ENTREGUE;
    }

    public boolean isCancelado() {
        return this == CANCELADO;
    }

    public boolean isEmAndamento() {
        return this == ACEITO || this == EM_ROTA || this == CHEGOU_DESTINO || this == PROBLEMA;
    }
}

