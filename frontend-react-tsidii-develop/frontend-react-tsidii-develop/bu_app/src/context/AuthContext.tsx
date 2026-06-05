import {
    createContext,
    useCallback,
    useEffect,
    useMemo,
    useState,
    type ReactNode,
} from 'react';
import { getCurrentUser } from '../services/auth.service';
import type { CurrentUser } from '../types/api';

type AuthState = {
    token: string | null;
    user: CurrentUser | null;
    isLoading: boolean;
};

export type AuthContextValue = {
    token: string | null;
    user: CurrentUser | null;
    isAuthenticated: boolean;
    isLoading: boolean;
    login: (newToken: string) => Promise<void>;
    logout: () => void;
    refreshMe: () => Promise<void>;
};

type AuthProviderProps = {
    children: ReactNode;
};

const TOKEN_STORAGE_KEY = 'token';

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

function normalizeToken(token: string): string {
    return token.replace(/^Bearer\s+/i, '').trim();
}

function getInitialAuthState(): AuthState {
    const storedToken = localStorage.getItem(TOKEN_STORAGE_KEY);

    if (!storedToken) {
        return { token: null, user: null, isLoading: false };
    }

    const normalizedToken = normalizeToken(storedToken);

    return { token: normalizedToken, user: null, isLoading: true };
}

export function AuthProvider({ children }: AuthProviderProps) {
    const [authState, setAuthState] = useState<AuthState>(() => getInitialAuthState());

    const logout = useCallback(() => {
        setAuthState({ token: null, user: null, isLoading: false });
        localStorage.removeItem(TOKEN_STORAGE_KEY);
    }, []);

    const refreshMe = useCallback(async () => {
        try {
            const user = await getCurrentUser();
            setAuthState((current) => ({ ...current, user, isLoading: false }));
        } catch {
            logout();
        }
    }, [logout]);

    useEffect(() => {
        if (!authState.token) {
            return;
        }

        if (authState.user) {
            return;
        }

        void getCurrentUser()
            .then((user) => {
                setAuthState((current) => ({ ...current, user, isLoading: false }));
            })
            .catch(() => {
                logout();
            });
    }, [authState.token, authState.user, logout]);

    const login = useCallback(
        async (newToken: string) => {
            const normalizedToken = normalizeToken(newToken);

            if (!normalizedToken) {
                logout();
                return;
            }

            setAuthState({ token: normalizedToken, user: null, isLoading: true });
            localStorage.setItem(TOKEN_STORAGE_KEY, normalizedToken);
            await refreshMe();
        },
        [logout, refreshMe],
    );

    const value = useMemo<AuthContextValue>(
        () => ({
            token: authState.token,
            user: authState.user,
            isAuthenticated: Boolean(authState.token && authState.user),
            isLoading: authState.isLoading,
            login,
            logout,
            refreshMe,
        }),
        [authState.token, authState.user, authState.isLoading, login, logout, refreshMe],
    );

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export default AuthContext;
