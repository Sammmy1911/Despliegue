import React from 'react';
import { Box, Alert, AlertTitle, Button, Stack } from '@mui/material';

interface ErrorProps {
    message: string;
    onRetry?: () => void;
}

export const ErrorDisplay: React.FC<ErrorProps> = ({ message, onRetry }) => {
    return (
        <Box sx={{ p: 3 }}>
            <Alert severity="error">
                <AlertTitle>Error</AlertTitle>
                {message}
            </Alert>
            {onRetry && (
                <Stack sx={{ mt: 2 }}>
                    <Button variant="contained" color="error" onClick={onRetry}>
                        Retry
                    </Button>
                </Stack>
            )}
        </Box>
    );
};
