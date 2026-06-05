import { Box, CircularProgress } from '@mui/material';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/useAuth';
import { normalizeRole } from '../utils/roles';

type ProtectedRouteProps = {
    allowedRoles?: string[];
};

export default function ProtectedRoute({ allowedRoles }: ProtectedRouteProps) {
    const { isAuthenticated, isLoading, user } = useAuth();

    if (isLoading) {
        return (
            <Box sx={{ minHeight: '100vh', display: 'grid', placeItems: 'center' }}>
                <CircularProgress />
            </Box>
        );
    }

    if (!isAuthenticated) {
        return <Navigate to="/auth/login" replace />;
    }

    if (allowedRoles && allowedRoles.length > 0) {
        const userRole = normalizeRole(user?.role);

        if (!allowedRoles.includes(userRole)) {
            return <Navigate to="/app" replace />;
        }
    }

    return <Outlet />;
}
