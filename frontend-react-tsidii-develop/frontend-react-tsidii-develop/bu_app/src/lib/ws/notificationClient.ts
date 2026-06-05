import { Client } from '@stomp/stompjs';
import type { IMessage, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export type AlertMessage = {
    type?: string;
    data?: AlertMessage | AlertMessage[];
    id?: number;
    title?: string;
    message?: string;
    body?: string;
    sendDate?: string;
    trainerCode?: number;
    traineeCode?: number;
};

type Callback = (payload: AlertMessage) => void;

type StompClientWithHeaders = Client & { connectHeaders?: Record<string, string> };

class NotificationClient {
    private client: Client | null = null;
    private subscription: StompSubscription | null = null;
    private url: string | null;
    private listeners: Callback[] = [];
    private pollingTimer: number | null = null;

    constructor(url?: string | null) {
        const apiBase = import.meta.env.VITE_API_URL ?? '';
        const wsEnvUrl = import.meta.env.VITE_WS_URL ?? '';
        const derivedUrl = wsEnvUrl || (apiBase ? `${apiBase.replace(/\/rest\/?$/, '')}/ws` : null);
        this.url = url ?? derivedUrl;

        if (this.url) {
            this.connectStomp();
        } else {
            this.startPolling();
        }
    }

    onMessage(cb: Callback) {
        this.listeners.push(cb);
    }

    offMessage(cb: Callback) {
        this.listeners = this.listeners.filter((c) => c !== cb);
    }

    private emit(payload: AlertMessage) {
        for (const cb of this.listeners) cb(payload);
    }

    private connectStomp() {
        try {
            const stompClient = new Client({
                webSocketFactory: () => new SockJS(this.url as string),
                reconnectDelay: 3000,
                debug: () => undefined,
            }) as StompClientWithHeaders;

            // attach Authorization header if token present
            try {
                const token = localStorage.getItem('token')?.replace(/^Bearer\s+/i, '').trim();
                if (token) {
                    stompClient.connectHeaders = { Authorization: `Bearer ${token}` };
                }
            } catch {
                // ignore
            }

            stompClient.onConnect = () => {
                this.subscription = stompClient.subscribe('/topic/alerts', (message: IMessage) => {
                    try {
                        const body = JSON.parse(message.body) as AlertMessage;
                        this.emit(body);
                    } catch {
                        this.emit({ message: message.body } as AlertMessage);
                    }
                });
            };

            stompClient.onStompError = () => {
                this.startPolling();
            };

            stompClient.activate();
            this.client = stompClient;
        } catch {
            this.startPolling();
        }
    }

    private startPolling(interval = 10000) {
        const poll = async () => {
            try {
                const apiBase = import.meta.env.VITE_API_URL ?? '';
                const alertsUrl = apiBase
                    ? `${apiBase.replace(/\/rest\/?$/, '')}/rest/alerts/all`
                    : '/alerts/all';

                const res = await fetch(alertsUrl, {
                    headers: { 'Content-Type': 'application/json' },
                });

                if (res.ok) {
                    const data = (await res.json()) as AlertMessage[];
                    this.emit({ type: 'poll', data });
                }
            } catch {
                // ignore
            }
        };

        void poll();
        this.pollingTimer = window.setInterval(poll, interval);
    }

    dispose() {
        if (this.subscription) {
            try {
                this.subscription.unsubscribe();
            } catch {
                // ignore
            }
            this.subscription = null;
        }

        if (this.client) {
            try {
                this.client.deactivate();
            } catch {
                // ignore
            }
            this.client = null;
        }

        if (this.pollingTimer) {
            clearInterval(this.pollingTimer);
            this.pollingTimer = null;
        }
    }
}

export default NotificationClient;
