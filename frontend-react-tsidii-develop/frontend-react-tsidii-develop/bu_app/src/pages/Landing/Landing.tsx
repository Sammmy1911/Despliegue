import { Box, Button, Container, Stack, Typography } from '@mui/material';
import { Link } from 'react-router-dom';

function Landing() {
    return (
        <Box
            sx={{
                minHeight: '100vh',
                display: 'grid',
                placeItems: 'center',
                px: 2,
            }}
        >
            <Container maxWidth="md">
                <Box
                    sx={{
                        p: { xs: 4, md: 6 },
                        bgcolor: 'white',
                    }}
                >
                    <Stack spacing={3}>
                        <Typography variant="h3" component="h1" sx={{ fontWeight: 700 }}>
                            BU App
                        </Typography>
                        <Typography color="text.secondary">
                            Aplicación de actividad física – Universidad Icesi
                        </Typography>
                        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                            <Button variant="contained" component={Link} to="/auth/login">
                                Ir al login
                            </Button>
                            <Button variant="outlined" component={Link} to="/app">
                                Ir al panel
                            </Button>
                        </Stack>
                    </Stack>
                </Box>
            </Container>
        </Box>
    );
}

export default Landing;
