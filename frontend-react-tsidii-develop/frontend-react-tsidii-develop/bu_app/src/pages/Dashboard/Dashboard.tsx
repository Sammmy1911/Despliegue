import { Card, CardContent, Grid, Stack, Typography } from '@mui/material';
import { useAuth } from '../../context/useAuth';

const cards = [
    {
        title: 'Rutinas activas',
        description: 'Crea y edita planes de entrenamiento con ejercicios globales y personalizados.',
    },
    {
        title: 'Seguimiento',
        description: 'Registra repeticiones, tiempo e intensidad para medir tu avance diario o semanal.',
    },
    {
        title: 'Acompañamiento',
        description: 'Entrenadores pueden revisar rutinas y progreso de los usuarios asignados.',
    },
];

export default function Dashboard() {
    const { user } = useAuth();

    return (
        <Stack spacing={3}>
            <Typography variant="h4" sx={{ fontWeight: 700 }}>
                Hola, {user?.name}
            </Typography>
            <Typography color="text.secondary">
                Bienvenido al panel principal. Desde aquí puedes gestionar ejercicios, rutinas y progreso.
            </Typography>

            <Grid container spacing={2}>
                {cards.map((card) => (
                    <Grid key={card.title} size={{ xs: 12, md: 4 }}>
                        <Card sx={{ height: '100%' }}>
                            <CardContent>
                                <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>
                                    {card.title}
                                </Typography>
                                <Typography color="text.secondary">{card.description}</Typography>
                            </CardContent>
                        </Card>
                    </Grid>
                ))}
            </Grid>
        </Stack>
    );
}
