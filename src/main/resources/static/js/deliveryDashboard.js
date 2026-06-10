(function () {
    const API_BASE = '/delivery/api';
    const STATUS_LABELS = {
        PENDENTE: 'Pendente',
        ACEITO: 'Aceito',
        EM_ROTA: 'Em rota',
        CHEGOU_DESTINO: 'Chegou ao destino',
        ENTREGUE: 'Entregue',
        PROBLEMA: 'Ocorrencia',
        CANCELADO: 'Cancelado'
    };

    const STATUS_BADGES = {
        PENDENTE: 'bg-warning-subtle text-warning-emphasis',
        ACEITO: 'bg-info-subtle text-info-emphasis',
        EM_ROTA: 'bg-primary-subtle text-primary-emphasis',
        CHEGOU_DESTINO: 'bg-secondary-subtle text-secondary-emphasis',
        ENTREGUE: 'bg-success-subtle text-success-emphasis',
        PROBLEMA: 'bg-danger-subtle text-danger-emphasis',
        CANCELADO: 'bg-dark-subtle text-dark-emphasis'
    };

    let deliveries = [];
    let toastInstance;

    function qs(id) {
        return document.getElementById(id);
    }

    function formatCurrency(value) {
        return new Intl.NumberFormat('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        }).format(Number(value || 0));
    }

    function formatDateTime(value) {
        if (!value) {
            return 'Nao informado';
        }
        const date = new Date(value);
        if (Number.isNaN(date.getTime())) {
            return 'Nao informado';
        }
        return new Intl.DateTimeFormat('pt-BR', {
            dateStyle: 'short',
            timeStyle: 'short'
        }).format(date);
    }

    function escapeHtml(value) {
        return String(value ?? '')
            .replaceAll('&', '&amp;')
            .replaceAll('<', '&lt;')
            .replaceAll('>', '&gt;')
            .replaceAll('"', '&quot;')
            .replaceAll("'", '&#39;');
    }

    function showToast(message) {
        const toastEl = qs('delivery-toast');
        const body = qs('delivery-toast-body');
        if (!toastEl || !body) {
            return;
        }
        body.textContent = message;
        toastInstance = toastInstance || bootstrap.Toast.getOrCreateInstance(toastEl);
        toastInstance.show();
    }

    function setSyncHint(message, isError) {
        const hint = qs('sync-hint');
        if (!hint) {
            return;
        }
        hint.textContent = message || '';
        hint.classList.toggle('text-danger', Boolean(isError));
        hint.classList.toggle('text-muted', !isError);
    }

    function updateConnectivityUi() {
        const indicator = qs('offline-indicator');
        if (!indicator) {
            return;
        }
        const isOnline = navigator.onLine;
        indicator.className = `offline-pill ${isOnline ? 'bg-success-subtle text-success' : 'bg-warning-subtle text-warning-emphasis'}`;
        indicator.innerHTML = isOnline
            ? '<i class="bi bi-wifi"></i> Online'
            : '<i class="bi bi-wifi-off"></i> Offline';

        const queueSize = window.DeliveryOfflineQueue ? window.DeliveryOfflineQueue.read().length : 0;
        setSyncHint(
            isOnline
                ? (queueSize ? `Existem ${queueSize} atualizacao(oes) aguardando sincronizacao.` : 'Dados sincronizados em tempo real.')
                : `Modo offline ativo. ${queueSize} atualizacao(oes) na fila local.`,
            false
        );
    }

    function buildQuery() {
        const params = new URLSearchParams();
        const status = qs('filter-status')?.value?.trim();
        const region = qs('filter-region')?.value?.trim();
        const deadline = qs('filter-deadline')?.value;

        if (status) {
            params.set('status', status);
        }
        if (region) {
            params.set('regiao', region);
        }
        if (deadline) {
            params.set('prazoAntesDe', new Date(deadline).toISOString().slice(0, 19));
        }
        return params.toString() ? `?${params}` : '';
    }

    function getAddressLine(delivery) {
        const address = [
            delivery.logradouroEntrega,
            delivery.numeroEntrega,
            delivery.bairroEntrega,
            delivery.cidadeEntrega,
            delivery.estadoEntrega
        ].filter(Boolean).join(', ');
        return address || 'Endereco nao informado';
    }

    function buildActions(delivery) {
        const actions = [];
        const status = delivery.statusEntrega;

        if (status === 'PENDENTE') {
            actions.push(buttonMarkup('Aceitar pedido', 'bi bi-check2-circle', 'btn btn-success btn-sm js-status-action', 'ACEITO', delivery.id));
            actions.push(buttonMarkup('Cancelar', 'bi bi-x-circle', 'btn btn-outline-dark btn-sm js-status-action', 'CANCELADO', delivery.id));
        }
        if (status === 'ACEITO') {
            actions.push(buttonMarkup('Iniciar rota', 'bi bi-sign-turn-right', 'btn btn-primary btn-sm js-status-action', 'EM_ROTA', delivery.id));
        }
        if (status === 'EM_ROTA') {
            actions.push(buttonMarkup('Cheguei ao destino', 'bi bi-geo-alt-fill', 'btn btn-secondary btn-sm js-status-action', 'CHEGOU_DESTINO', delivery.id));
            actions.push(buttonMarkup('Registrar problema', 'bi bi-exclamation-triangle', 'btn btn-outline-danger btn-sm js-status-action', 'PROBLEMA', delivery.id));
        }
        if (status === 'CHEGOU_DESTINO') {
            actions.push(buttonMarkup('Confirmar entrega', 'bi bi-camera-fill', 'btn btn-success btn-sm js-status-action', 'ENTREGUE', delivery.id));
            actions.push(buttonMarkup('Registrar problema', 'bi bi-exclamation-triangle', 'btn btn-outline-danger btn-sm js-status-action', 'PROBLEMA', delivery.id));
        }
        if (status === 'PROBLEMA') {
            actions.push(buttonMarkup('Retomar entrega', 'bi bi-arrow-repeat', 'btn btn-primary btn-sm js-status-action', 'EM_ROTA', delivery.id));
            actions.push(buttonMarkup('Cancelar', 'bi bi-x-circle', 'btn btn-outline-dark btn-sm js-status-action', 'CANCELADO', delivery.id));
        }

        const mapsQuery = encodeURIComponent(getAddressLine(delivery));
        actions.push(
            `<a class="btn btn-soft btn-sm" target="_blank" rel="noopener noreferrer" href="https://www.google.com/maps/search/?api=1&query=${mapsQuery}">` +
            `<i class="bi bi-map me-1"></i>Navegar</a>`
        );
        return actions.join('');
    }

    function buttonMarkup(label, icon, cssClass, status, deliveryId) {
        return `<button type="button" class="${cssClass}" data-status="${status}" data-id="${deliveryId}">` +
            `<i class="${icon} me-1"></i>${label}</button>`;
    }

    function updateKpis(items) {
        qs('kpi-pending').textContent = items.filter((item) => item.statusEntrega === 'PENDENTE').length;
        qs('kpi-progress').textContent = items.filter((item) => ['ACEITO', 'EM_ROTA', 'CHEGOU_DESTINO', 'PROBLEMA'].includes(item.statusEntrega)).length;
        qs('kpi-done').textContent = items.filter((item) => item.statusEntrega === 'ENTREGUE').length;
    }

    function renderDeliveries(items) {
        const list = qs('delivery-list');
        const empty = qs('delivery-empty');
        if (!list || !empty) {
            return;
        }

        if (!items.length) {
            list.innerHTML = '';
            empty.classList.remove('d-none');
            updateKpis(items);
            return;
        }

        empty.classList.add('d-none');
        list.innerHTML = items.map((delivery) => {
            const badgeClass = STATUS_BADGES[delivery.statusEntrega] || 'bg-light text-dark';
            const occurrence = delivery.ocorrenciaEntrega
                ? `<div class="mt-2 small text-danger"><i class="bi bi-exclamation-circle me-1"></i>${escapeHtml(delivery.ocorrenciaEntrega)}</div>`
                : '';
            return `
                <article class="delivery-card mb-3">
                    <div class="d-flex justify-content-between align-items-start gap-3 flex-wrap">
                        <div>
                            <div class="delivery-title">${escapeHtml(delivery.codigoPedido)} - ${escapeHtml(delivery.clienteNome)}</div>
                            <div class="meta-line mt-1"><i class="bi bi-geo-alt me-1"></i>${escapeHtml(getAddressLine(delivery))}</div>
                            <div class="meta-line"><i class="bi bi-stopwatch me-1"></i>Prazo: ${escapeHtml(formatDateTime(delivery.prazoEntrega))}</div>
                            <div class="meta-line"><i class="bi bi-cash-coin me-1"></i>Frete: ${escapeHtml(formatCurrency(delivery.valorFrete))}</div>
                        </div>
                        <span class="status-pill ${badgeClass}">
                            <i class="bi bi-truck"></i>${escapeHtml(STATUS_LABELS[delivery.statusEntrega] || delivery.statusEntrega || 'Pendente')}
                        </span>
                    </div>
                    ${occurrence}
                    <div class="action-wrap">${buildActions(delivery)}</div>
                </article>
            `;
        }).join('');

        updateKpis(items);
    }

    async function loadDeliveries() {
        const refreshBtn = qs('btn-refresh');
        if (refreshBtn) {
            refreshBtn.disabled = true;
        }
        try {
            const response = await fetch(`${API_BASE}/pedidos${buildQuery()}`, {
                headers: { 'Accept': 'application/json' }
            });
            if (!response.ok) {
                throw new Error('Nao foi possivel carregar as entregas.');
            }
            deliveries = await response.json();
            renderDeliveries(deliveries);
            updateConnectivityUi();
        } catch (error) {
            renderDeliveries([]);
            setSyncHint(error.message || 'Falha ao carregar entregas.', true);
        } finally {
            if (refreshBtn) {
                refreshBtn.disabled = false;
            }
        }
    }

    async function sendStatusUpdate(deliveryId, payload) {
        const response = await fetch(`${API_BASE}/pedidos/${deliveryId}/status`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const message = await response.text();
            throw new Error(message || 'Nao foi possivel atualizar o status.');
        }
        return response.json();
    }

    function collectEvidence(status) {
        if (status === 'ENTREGUE') {
            return {
                assinaturaEntrega: window.prompt('Informe a assinatura digital do recebedor (nome ou hash):', '') || '',
                comprovanteEntrega: window.prompt('Cole a foto/comprovante em URL ou Base64:', '') || '',
                ocorrenciaEntrega: ''
            };
        }
        if (status === 'PROBLEMA') {
            return {
                assinaturaEntrega: '',
                comprovanteEntrega: '',
                ocorrenciaEntrega: window.prompt('Descreva a ocorrencia da entrega:', '') || 'Ocorrencia nao informada'
            };
        }
        return {
            assinaturaEntrega: '',
            comprovanteEntrega: '',
            ocorrenciaEntrega: ''
        };
    }

    async function queueOrSendStatus(deliveryId, status) {
        const payload = { statusEntrega: status, ...collectEvidence(status) };

        if (!navigator.onLine && window.DeliveryOfflineQueue) {
            window.DeliveryOfflineQueue.enqueue({
                deliveryId,
                payload
            });
            updateConnectivityUi();
            showToast('Atualizacao salva offline. Ela sera sincronizada automaticamente.');
            return;
        }

        await sendStatusUpdate(deliveryId, payload);
        showToast('Status da entrega atualizado com sucesso.');
        await loadDeliveries();
    }

    async function syncOfflineQueue() {
        if (!window.DeliveryOfflineQueue) {
            return;
        }
        if (!navigator.onLine) {
            showToast('Conecte-se a internet para sincronizar a fila offline.');
            return;
        }

        const result = await window.DeliveryOfflineQueue.drain(async (action) => {
            await sendStatusUpdate(action.deliveryId, action.payload);
        });

        if (result.processed > 0) {
            showToast(`${result.processed} atualizacao(oes) offline sincronizada(s).`);
        }
        updateConnectivityUi();
        await loadDeliveries();
    }

    function openOptimizedRoute() {
        if (!deliveries.length) {
            showToast('Nao ha entregas disponiveis para montar a rota.');
            return;
        }

        const routeDeliveries = deliveries
            .filter((delivery) => !['ENTREGUE', 'CANCELADO'].includes(delivery.statusEntrega))
            .sort((a, b) => String(a.prazoEntrega || '').localeCompare(String(b.prazoEntrega || '')));

        if (!routeDeliveries.length) {
            showToast('Nao ha entregas ativas para a rota otimizada.');
            return;
        }

        const destination = getAddressLine(routeDeliveries[routeDeliveries.length - 1]);
        const waypoints = routeDeliveries
            .slice(0, -1)
            .map((delivery) => getAddressLine(delivery))
            .join('|');

        const url = new URL('https://www.google.com/maps/dir/');
        url.searchParams.set('api', '1');
        url.searchParams.set('destination', destination);
        url.searchParams.set('travelmode', 'driving');
        if (waypoints) {
            url.searchParams.set('waypoints', waypoints);
        }
        window.open(url.toString(), '_blank', 'noopener');
    }

    function handleStatusAction(event) {
        const button = event.target.closest('.js-status-action');
        if (!button) {
            return;
        }
        const deliveryId = Number(button.dataset.id);
        const status = button.dataset.status;
        button.disabled = true;
        queueOrSendStatus(deliveryId, status)
            .catch((error) => {
                showToast(error.message || 'Falha ao atualizar a entrega.');
                setSyncHint(error.message || 'Falha ao atualizar a entrega.', true);
            })
            .finally(() => {
                button.disabled = false;
            });
    }

    function requestNotificationPermission() {
        if (!('Notification' in window) || Notification.permission !== 'default') {
            return;
        }
        Notification.requestPermission().catch(() => undefined);
    }

    function emitBrowserNotification(title, body) {
        if (!('Notification' in window) || Notification.permission !== 'granted') {
            return;
        }
        new Notification(title, { body });
    }

    function initSse() {
        if (!window.EventSource) {
            return;
        }
        const source = new EventSource(`${API_BASE}/stream`);
        const onDeliveryEvent = async (event) => {
            try {
                const payload = JSON.parse(event.data);
                showToast(payload.mensagem || 'Atualizacao recebida.');
                emitBrowserNotification('Farm Food Entregas', payload.mensagem || 'Atualizacao recebida.');
                await loadDeliveries();
            } catch (_) {
                showToast('Atualizacao recebida.');
                await loadDeliveries();
            }
        };

        source.addEventListener('novo-pedido', onDeliveryEvent);
        source.addEventListener('status-entrega', onDeliveryEvent);
        source.addEventListener('sync', () => updateConnectivityUi());
        source.onerror = () => {
            setSyncHint('Reconectando ao canal em tempo real...', false);
        };
    }

    function attachEvents() {
        qs('btn-refresh')?.addEventListener('click', loadDeliveries);
        qs('btn-sync-queue')?.addEventListener('click', () => {
            syncOfflineQueue().catch((error) => {
                setSyncHint(error.message || 'Falha ao sincronizar a fila offline.', true);
            });
        });
        qs('btn-open-route')?.addEventListener('click', openOptimizedRoute);
        qs('delivery-list')?.addEventListener('click', handleStatusAction);
        qs('filter-status')?.addEventListener('change', loadDeliveries);
        qs('filter-region')?.addEventListener('input', debounce(loadDeliveries, 350));
        qs('filter-deadline')?.addEventListener('change', loadDeliveries);

        window.addEventListener('online', () => {
            updateConnectivityUi();
            syncOfflineQueue().catch(() => undefined);
        });
        window.addEventListener('offline', updateConnectivityUi);
    }

    function debounce(fn, delay) {
        let timeoutId;
        return function debounced() {
            window.clearTimeout(timeoutId);
            timeoutId = window.setTimeout(() => fn(), delay);
        };
    }

    document.addEventListener('DOMContentLoaded', () => {
        attachEvents();
        updateConnectivityUi();
        requestNotificationPermission();
        loadDeliveries();
        initSse();
    });
})();
