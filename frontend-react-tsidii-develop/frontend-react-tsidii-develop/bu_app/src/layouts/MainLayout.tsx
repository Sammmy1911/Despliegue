import React from 'react';
import { AppBar, Toolbar, Typography, Button, Container, Box, Menu, MenuItem } from '@mui/material';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import LogoutIcon from '@mui/icons-material/Logout';
import { useAppSelector, useAppDispatch } from '../store/hooks';
import { clearAuth } from '../store/slices/authSlice';

interface MainLayoutProps {
    children: React.ReactNode;
}

export const MainLayout: React.FC<MainLayoutProps> = ({ children }) => {
    const navigate = useNavigate();
    const dispatch = useAppDispatch();
    const { user, isAuthenticated } = useAppSelector((state) => state.auth);
    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);

    const handleMenuOpen = (event: React.MouseEvent<HTMLElement>): void => {
        setAnchorEl(event.currentTarget);
    };

    const handleMenuClose = (): void => {
        setAnchorEl(null);
    };

    const handleLogout = (): void => {
        dispatch(clearAuth());
        localStorage.removeItem('token');
        navigate('/auth/login');
    };

    return (
        <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <AppBar position="sticky">
                <Toolbar>
                    <Typography
                        variant="h6"
                        component={RouterLink}
                        to="/"
                        sx={{
                            flexGrow: 1,
                            textDecoration: 'none',
                            color: 'inherit',
                            fontWeight: 'bold',
                        }}
                    >
                        Fitness Platform
                    </Typography>

                    {isAuthenticated && (
                        <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
                            <Button
                                color="inherit"
                                component={RouterLink}
                                to="/dashboard"
                                sx={{ textTransform: 'none' }}
                            >
                                Dashboard
                            </Button>
                            <Button
                                color="inherit"
                                component={RouterLink}
                                to="/events"
                                sx={{ textTransform: 'none' }}
                            >
                                Events
                            </Button>

                            <Button
                                onClick={handleMenuOpen}
                                startIcon={<AccountCircleIcon />}
                                sx={{ color: 'inherit' }}
                            >
                                {user?.email?.split('@')[0]}
                            </Button>
                            <Menu
                                anchorEl={anchorEl}
                                open={Boolean(anchorEl)}
                                onClose={handleMenuClose}
                            >
                                <MenuItem disabled>{user?.email || 'User'}</MenuItem>
                                <MenuItem onClick={handleLogout} sx={{ gap: 1 }}>
                                    <LogoutIcon fontSize="small" />
                                    Logout
                                </MenuItem>
                            </Menu>
                        </Box>
                    )}
                </Toolbar>
            </AppBar>

            <Box component="main" sx={{ flexGrow: 1 }}>
                {children}
            </Box>

            <Box component="footer" sx={{ py: 3, bgcolor: 'background.paper', mt: 'auto' }}>
                <Container maxWidth="lg">
                    <Typography variant="body2" color="textSecondary" align="center">
                        © {new Date().getFullYear()} Fitness Platform. All rights reserved.
                    </Typography>
                </Container>
            </Box>
        </Box>
    );
};
