import {
    AppBar,
    Avatar,
    Box,
    Button,
    Chip,
    Container,
    Drawer,
    List,
    ListItemButton,
    ListItemText,
    Stack,
    Toolbar,
    Typography,
} from '@mui/material';
import FitnessCenterIcon from '@mui/icons-material/FitnessCenter';
import MonitorHeartIcon from '@mui/icons-material/MonitorHeart';
import DirectionsRunIcon from '@mui/icons-material/DirectionsRun';
import DashboardIcon from '@mui/icons-material/Dashboard';
import SupervisorAccountIcon from '@mui/icons-material/SupervisorAccount';
import EventAvailableIcon from '@mui/icons-material/EventAvailable';
import NotificationsBell from './NotificationsBell';
import { Link as RouterLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { useMemo, type ReactNode } from 'react';
import { useAuth } from '../context/useAuth';
import { normalizeRole } from '../utils/roles';

type NavItem = {
    label: string;
    to: string;
    icon: ReactNode;
    showFor: string[];
};

const drawerWidth = 260;

const navItems: NavItem[] = [
    {
        label: 'Dashboard',
        to: '/app',
        icon: <DashboardIcon fontSize="small" />,
        showFor: ['ADMIN', 'TRAINER', 'TRAINEE'],
    },
    {
        label: 'Ejercicios',
        to: '/app/exercises',
        icon: <FitnessCenterIcon fontSize="small" />,
        showFor: ['ADMIN', 'TRAINER', 'TRAINEE'],
    },
    {
        label: 'Rutinas',
        to: '/app/routines',
        icon: <DirectionsRunIcon fontSize="small" />,
        showFor: ['ADMIN', 'TRAINER', 'TRAINEE'],
    },
    {
        label: 'Eventos',
        to: '/events',
        icon: <EventAvailableIcon fontSize="small" />,
        showFor: ['ADMIN', 'TRAINER', 'TRAINEE'],
    },
    {
        label: 'Progreso',
        to: '/app/progress',
        icon: <MonitorHeartIcon fontSize="small" />,
        showFor: ['ADMIN', 'TRAINER', 'TRAINEE'],
    },
    {
        label: 'Panel entrenador',
        to: '/app/trainer',
        icon: <SupervisorAccountIcon fontSize="small" />,
        showFor: ['ADMIN', 'TRAINER'],
    },
    {
        label: 'Administración',
        to: '/app/admin',
        icon: <SupervisorAccountIcon fontSize="small" />,
        showFor: ['ADMIN'],
    },
    {
        label: 'Eventos',
        to: '/app/admin/events',
        icon: <SupervisorAccountIcon fontSize="small" />,
        showFor: ['ADMIN'],
    },
];

export default function AppShell() {
    const { pathname } = useLocation();
    const navigate = useNavigate();
    const { user, logout } = useAuth();

    const role = normalizeRole(user?.role as string | undefined);

    const filteredItems = useMemo(
        () => navItems.filter((item) => item.showFor.includes(role)),
        [role],
    );

    const initials = (user?.name as string | undefined)
        ?.split(' ')
        .filter((part: string) => part.length > 0)
        .map((part: string) => part[0]?.toUpperCase())
        .join('')
        .slice(0, 2);

    const handleLogout = () => {
        logout();
        navigate('/auth/login', { replace: true });
    };

    return (
        <Box sx={{ display: 'flex', minHeight: '100vh', bgcolor: 'grey.100' }}>
            <AppBar
                position="fixed"
                sx={{
                    zIndex: (theme) => theme.zIndex.drawer + 1,
                    bgcolor: 'background.paper',
                    color: 'text.primary',
                    borderBottom: 1,
                    borderColor: 'divider',
                    boxShadow: 'none',
                }}
            >
                <Toolbar>
                    <Stack direction="row" spacing={2} sx={{ alignItems: 'center', width: '100%' }}>
                        <Typography variant="h6" sx={{ fontWeight: 700 }}>
                            BU App
                        </Typography>
                        <Chip label={role || 'SIN ROL'} color="primary" size="small" />
                        <Box sx={{ flexGrow: 1 }} />
                        <Stack direction="row" spacing={1.5} sx={{ alignItems: 'center' }}>
                            <NotificationsBell />
                            <Avatar>{initials ?? 'U'}</Avatar>
                            <Box>
                                <Typography variant="body2" sx={{ fontWeight: 600 }}>
                                    {(user?.name as string | undefined) ?? '-'}
                                </Typography>
                                <Typography variant="caption" color="text.secondary">
                                    {(user?.email as string | undefined) ?? '-'}
                                </Typography>
                            </Box>
                            <Button variant="outlined" onClick={handleLogout}>
                                Cerrar sesión
                            </Button>
                        </Stack>
                    </Stack>
                </Toolbar>
            </AppBar>

            <Drawer
                variant="permanent"
                sx={{
                    width: drawerWidth,
                    flexShrink: 0,
                    '& .MuiDrawer-paper': {
                        width: drawerWidth,
                        boxSizing: 'border-box',
                        borderRight: 1,
                        borderColor: 'divider',
                    },
                }}
            >
                <Toolbar />
                <List>
                    {filteredItems.map((item) => {
                        const selected = item.to === '/app' ? pathname === '/app' : pathname.startsWith(item.to);

                        return (
                            <ListItemButton key={item.to} component={RouterLink} to={item.to} selected={selected}>
                                <Stack direction="row" spacing={1.5} sx={{ alignItems: 'center' }}>
                                    {item.icon}
                                    <ListItemText primary={item.label} />
                                </Stack>
                            </ListItemButton>
                        );
                    })}
                </List>
            </Drawer>

            <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
                <Toolbar />
                <Container maxWidth="xl">
                    <Outlet />
                </Container>
            </Box>
        </Box>
    );
}
