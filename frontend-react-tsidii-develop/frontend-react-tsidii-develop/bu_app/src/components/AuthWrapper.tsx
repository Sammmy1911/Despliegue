import React from 'react';
import { Navigate } from 'react-router-dom';

function parseJwt(token: string | null): Record<string, unknown> | null {
    if (!token) return null;
    try {
        const payload = token.split('.')[1];
        const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
        return JSON.parse(decodeURIComponent(escape(decoded)));
    } catch {
        return null;
    }
}

type Props = {
  children: React.ReactNode
  requiredRole?: string
}

const AuthWrapper: React.FC<Props> = ({ children, requiredRole }) => {
    const token = localStorage.getItem('token');
    if (!token) return <Navigate to="/" replace />;

    if (requiredRole) {
        const payload = parseJwt(token);
        const roles = payload?.roles || payload?.role || [];
        const hasRole = Array.isArray(roles) ? roles.includes(requiredRole) : roles === requiredRole;
        if (!hasRole) return <Navigate to="/" replace />;
    }

    return <>{children}</>;
};

export default AuthWrapper;
