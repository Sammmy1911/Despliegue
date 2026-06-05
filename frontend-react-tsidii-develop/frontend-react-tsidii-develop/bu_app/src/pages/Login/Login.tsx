import { Box, Button, Container, Paper, Stack, TextField, Typography } from '@mui/material';
import { useState, type SubmitEvent } from 'react';
import { loginRequest } from '../../services/auth.service';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/useAuth';

type LoginFormData = {
    username: string;
    password: string;
};

function SignIn() {
    const nav = useNavigate();
    const { login: setAuthToken } = useAuth();
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    const onSubmit = async (event: SubmitEvent<HTMLFormElement>) => {
        event.preventDefault();
        setErrorMessage(null);

        const formData = new FormData(event.currentTarget);
        const username = formData.get('username');
        const password = formData.get('password');

        if (typeof username !== 'string' || typeof password !== 'string') {
            return;
        }

        const credentials: LoginFormData = {
            username,
            password,
        };

        try {
            const token = await loginRequest(credentials.username, credentials.password);
            await setAuthToken(token);
            nav('/app', { replace: true });
        } catch {
            setErrorMessage('No fue posible iniciar sesión. Verifica tus credenciales e intenta de nuevo.');
        }
    };

    return (
        <Box sx={{ minHeight: '100vh', display: 'grid', placeItems: 'center', px: 2 }}>
            <Container maxWidth="sm">
                <Paper elevation={3} sx={{ p: { xs: 3, md: 4 } }}>
                    <Stack spacing={3} component="form" onSubmit={onSubmit}>
                        <Box>
                            <Typography variant="h4" component="h1" sx={{ fontWeight: 700 }}>
                                Login
                            </Typography>
                        </Box>

                        <TextField label="Username" name="username" fullWidth />

                        <TextField label="Password" name="password" type="password" fullWidth />

                        {errorMessage ? <Typography color="error">{errorMessage}</Typography> : null}

                        <Button type="submit" variant="contained">
                            Entrar
                        </Button>
                    </Stack>
                </Paper>
            </Container>
        </Box>
    );
}

export default SignIn;
