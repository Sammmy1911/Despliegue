declare module 'sockjs-client' {
    class SockJS {
        constructor(url: string, _options?: unknown);
        close(): void;
    }

    export default SockJS;
}
