(function () {
    const STORAGE_KEY = 'farmfood_delivery_offline_queue';

    function read() {
        try {
            const raw = localStorage.getItem(STORAGE_KEY);
            const parsed = raw ? JSON.parse(raw) : [];
            return Array.isArray(parsed) ? parsed : [];
        } catch (_) {
            return [];
        }
    }

    function write(queue) {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(queue));
    }

    function enqueue(action) {
        const queue = read();
        queue.push({
            id: Date.now() + '-' + Math.random().toString(16).slice(2),
            createdAt: new Date().toISOString(),
            ...action
        });
        write(queue);
        return queue;
    }

    async function drain(processor) {
        const queue = read();
        if (!queue.length) {
            return { processed: 0, remaining: 0 };
        }

        const pending = [];
        let processed = 0;

        for (const action of queue) {
            try {
                await processor(action);
                processed += 1;
            } catch (_) {
                pending.push(action);
            }
        }

        write(pending);
        return { processed, remaining: pending.length };
    }

    window.DeliveryOfflineQueue = {
        read,
        write,
        enqueue,
        drain,
        STORAGE_KEY
    };
})();

