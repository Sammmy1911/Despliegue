import React from 'react';
import { Box, Breadcrumbs, IconButton, Link as MuiLink, Typography } from '@mui/material';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import HomeIcon from '@mui/icons-material/Home';
import ArrowBackIosNewIcon from '@mui/icons-material/ArrowBackIosNew';
import ArrowForwardIosIcon from '@mui/icons-material/ArrowForwardIos';

interface BreadcrumbItem {
    label: string;
    to?: string;
}

interface PageHeaderProps {
    breadcrumbItems: BreadcrumbItem[];
    title: string;
    subtitle?: string;
    showNavigation?: boolean;
    showBreadcrumb?: boolean;
}

export const PageHeader: React.FC<PageHeaderProps> = ({
    breadcrumbItems,
    title,
    subtitle,
    showNavigation = true,
    showBreadcrumb = true,
}) => {
    const navigate = useNavigate();

    return (
        <Box sx={{ mb: 4, display: 'flex', flexDirection: 'column', gap: 2 }}>
            <Box
                sx={{
                    display: 'flex',
                    flexDirection: { xs: 'column', sm: 'row' },
                    justifyContent: 'space-between',
                    alignItems: { xs: 'flex-start', sm: 'center' },
                    gap: 1,
                }}
            >
                {showBreadcrumb && (
                    <Breadcrumbs aria-label="breadcrumb" sx={{ fontSize: 14 }} separator="/">
                        <MuiLink
                            component={RouterLink}
                            to="/dashboard"
                            color="inherit"
                            underline="hover"
                            sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}
                        >
                            <HomeIcon fontSize="small" />
                            Inicio
                        </MuiLink>
                        {breadcrumbItems.map((item, index) =>
                            item.to ? (
                                <MuiLink
                                    key={item.label + index}
                                    component={RouterLink}
                                    to={item.to}
                                    color="inherit"
                                    underline="hover"
                                >
                                    {item.label}
                                </MuiLink>
                            ) : (
                                <Typography key={item.label + index} color="textPrimary">
                                    {item.label}
                                </Typography>
                            ),
                        )}
                    </Breadcrumbs>
                )}

                {showNavigation && (
                    <Box sx={{ display: 'flex', gap: 1 }}>
                        <IconButton
                            aria-label="Atrás"
                            onClick={() => navigate(-1)}
                            size="small"
                        >
                            <ArrowBackIosNewIcon fontSize="small" />
                        </IconButton>
                        <IconButton
                            aria-label="Adelante"
                            onClick={() => navigate(1)}
                            size="small"
                        >
                            <ArrowForwardIosIcon fontSize="small" />
                        </IconButton>
                    </Box>
                )}
            </Box>

            <Box>
                <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 'bold' }}>
                    {title}
                </Typography>
                {subtitle && (
                    <Typography variant="body1" color="textSecondary">
                        {subtitle}
                    </Typography>
                )}
            </Box>
        </Box>
    );
};
