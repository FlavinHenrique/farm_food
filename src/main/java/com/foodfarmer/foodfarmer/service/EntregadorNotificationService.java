package com.foodfarmer.foodfarmer.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class EntregadorNotificationService {
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long entregadorId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.computeIfAbsent(entregadorId, ignored -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> remove(entregadorId, emitter));
        emitter.onTimeout(() -> remove(entregadorId, emitter));
        emitter.onError((error) -> remove(entregadorId, emitter));
        return emitter;
    }

    public void send(Long entregadorId, String eventName, Object payload) {
        List<SseEmitter> targets = emitters.get(entregadorId);
        if (targets == null || targets.isEmpty()) {
            return;
        }

        for (SseEmitter emitter : targets) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(payload));
            } catch (IOException ex) {
                remove(entregadorId, emitter);
            }
        }
    }

    private void remove(Long entregadorId, SseEmitter emitter) {
        List<SseEmitter> targets = emitters.get(entregadorId);
        if (targets == null) {
            return;
        }
        targets.remove(emitter);
        if (targets.isEmpty()) {
            emitters.remove(entregadorId);
        }
    }
}

