(function () {
    const API_URL = '/delivery/api/pedidos?historico=true';

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

    function statusLabel(status) {
        switch (status) {
            case 'ENTREGUE':
                return 'Concluida';
            case 'PROBLEMA':
                return 'Com ocorrencia';
            case 'CANCELADO':
                return 'Cancelada';
            default:
                return status || 'Nao informado';
        }
    }

    function statusBadge(status) {
        switch (status) {
            case 'ENTREGUE':
                return 'bg-success-subtle text-success-emphasis';
            case 'PROBLEMA':
                return 'bg-warning-subtle text-warning-emphasis';
            case 'CANCELADO':
                return 'bg-danger-subtle text-danger-emphasis';
            default:
                return 'bg-secondary-subtle text-secondary-emphasis';
        }
    }

    function renderHistory(items) {
        const list = qs('history-list');
        const empty = qs('history-empty');
        if (!list || !empty) {
            return;
        }

        if (!items.length) {
            list.innerHTML = '';
            empty.classList.remove('d-none');
            return;
        }

        empty.classList.add('d-none');
        list.innerHTML = items.map((item) => {
            const address = [
                item.logradouroEntrega,
                item.numeroEntrega,
                item.bairroEntrega,
                item.cidadeEntrega,
                item.estadoEntrega
            ].filter(Boolean).join(', ');

            const occurrence = item.ocorrenciaEntrega
                ? `<div class="small text-danger mt-2"><i class="bi bi-exclamation-circle me-1"></i>${escapeHtml(item.ocorrenciaEntrega)}</div>`
                : '';

            return `
                <article class="history-card">
                    <div class="d-flex justify-content-between align-items-start gap-3 flex-wrap">
                        <div>
                            <div class="fw-bold">${escapeHtml(item.codigoPedido)} - ${escapeHtml(item.clienteNome)}</div>
                            <div class="meta mt-1"><i class="bi bi-geo-alt me-1"></i>${escapeHtml(address || 'Endereco nao informado')}</div>
                            <div class="meta"><i class="bi bi-calendar-event me-1"></i>Pedido: ${escapeHtml(formatDateTime(item.dataPedido))}</div>
                            <div class="meta"><i class="bi bi-check2-square me-1"></i>Fechamento: ${escapeHtml(formatDateTime(item.entregueEm || item.prazoEntrega))}</div>
                        </div>
                        <div class="text-end">
                            <div><span class="badge ${statusBadge(item.statusEntrega)}">${escapeHtml(statusLabel(item.statusEntrega))}</span></div>
                            <div class="money mt-2">${escapeHtml(formatCurrency(item.valorFrete))}</div>
                            <div class="meta">Frete recebido</div>
                        </div>
                    </div>
                    ${occurrence}
                </article>
            `;
        }).join('');
    }

    async function loadHistory() {
        const button = qs('btn-refresh-history');
        if (button) {
            button.disabled = true;
        }
        try {
            const response = await fetch(API_URL, { headers: { 'Accept': 'application/json' } });
            if (!response.ok) {
                throw new Error('Nao foi possivel carregar o historico.');
            }
            const items = await response.json();
            renderHistory(items);
        } catch (error) {
            const list = qs('history-list');
            const empty = qs('history-empty');
            if (list) {
                list.innerHTML = `<div class="alert alert-danger">${escapeHtml(error.message || 'Falha ao carregar o historico.')}</div>`;
            }
            if (empty) {
                empty.classList.add('d-none');
            }
        } finally {
            if (button) {
                button.disabled = false;
            }
        }
    }

    document.addEventListener('DOMContentLoaded', () => {
        qs('btn-refresh-history')?.addEventListener('click', loadHistory);
        loadHistory();
    });
})();
