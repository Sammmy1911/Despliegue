import React from 'react';
import { Box, Typography, Button, Stack } from '@mui/material';
import SearchOffIcon from '@mui/icons-material/SearchOff';

interface EmptyStateProps {
    title: string;
    description?: string;
    actionLabel?: string;
    onAction?: () => void;
}

export const EmptyState: React.FC<EmptyStateProps> = ({
    title,
    description,
    actionLabel,
    onAction,
}) => {
    return (
        <Box
            sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                minHeight: '300px',
                p: 3,
            }}
        >
            <SearchOffIcon sx={{ fontSize: 80, color: 'action.disabled', mb: 2 }} />
            <Typography variant="h6" align="center" gutterBottom>
                {title}
            </Typography>
            {description && (
                <Typography variant="body2" color="textSecondary" align="center" sx={{ mb: 3 }}>
                    {description}
                </Typography>
            )}
            {actionLabel && onAction && (
                <Stack>
                    <Button variant="contained" onClick={onAction}>
                        {actionLabel}
                    </Button>
                </Stack>
            )}
        </Box>
    );
};
