import React from 'react';
import { Box, CircularProgress, Typography, Stack } from '@mui/material';

interface LoadingProps {
    message?: string;
}

export const Loading: React.FC<LoadingProps> = ({ message = 'Loading...' }) => {
    return (
        <Box
            sx={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                minHeight: '400px',
            }}
        >
            <Stack sx={{ alignItems: 'center' }} spacing={2}>
                <CircularProgress />
                <Typography variant="body1">{message}</Typography>
            </Stack>
        </Box>
    );
};
