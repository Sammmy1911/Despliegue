import React, { useEffect, useState } from 'react';
import {Container,
    Typography, Box,Grid,Paper, Table,TableBody,
    TableCell,TableContainer,
    TableHead,TableRow,CircularProgress,Alert,Card,CardContent,
    Stack,Divider,
} from '@mui/material';
import {
    Timeline,
    TimelineItem,
    TimelineSeparator,
    TimelineConnector,
    TimelineContent,
    TimelineDot,
    TimelineOppositeContent,
} from '@mui/lab';
import FitnessCenterIcon from '@mui/icons-material/FitnessCenter';
import TimerIcon from '@mui/icons-material/Timer';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import { useAuth } from '../../context/useAuth';
import { progressService } from './services/progress.service';
import type { ProgressResponse } from './services/progress.service';

type AuthUser = {
    code: number;
    role: string;
};

const History: React.FC = () => {
    const { user } = useAuth();

    const authUser = user as AuthUser | null;

    const traineeId = authUser?.code;
    const userRole = authUser?.role;
    const canViewHistory = userRole === 'TRAINEE';

    const [progresses, setProgresses] = useState<ProgressResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    useEffect(() => {
        if (traineeId && canViewHistory) {
            progressService.getByTrainee(traineeId)
                .then(data => {
                    const sorted = data.sort((a, b) => 
                        new Date(b.performedAt).getTime() - new Date(a.performedAt).getTime()
                    );
                    setProgresses(sorted);
                    setLoading(false);
                })
                .catch(() => {
                    setError('Error al cargar el historial de actividades');
                    setLoading(false);
                });

        } else {
            // eslint-disable-next-line react-hooks/set-state-in-effect
            setLoading(false);
        }
    }, [traineeId, canViewHistory]);

    const totalWorkouts = progresses.length;
    const totalTime = progresses.reduce((acc, curr) => {
        const minutes = parseInt(curr.time) || 0;
        return acc + minutes;
    }, 0);
    const avgStress = progresses.length > 0 
        ? (progresses.reduce((acc, curr) => {
            const stressMap: Record<string, number> = { 'LOW': 1, 'MEDIUM': 2, 'HIGH': 3, 'EXTREME': 4 };
            return acc + (stressMap[curr.stressLevelName] || 0);
        }, 0) / progresses.length).toFixed(1)
        : 0;

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '80vh' }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Container maxWidth="lg">
            <Box sx={{ my: 4 }}>
                <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 'bold' }}>
                    Mi Historial y Rendimiento
                </Typography>

                {!canViewHistory && (
                    <Alert severity="warning" sx={{ mb: 2 }}>
                        Esta secciÃ³n estÃ¡ disponible solo para usuarios en entrenamiento.
                    </Alert>
                )}
                {/* Mensaje de error en caso de fallar la consulta al backend */}
                {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
                <Grid container spacing={3} sx={{ mb: 4 }}>
                    <Grid size={{ xs: 12, sm: 4 }}>
                        {/* Tarjeta: cantidad total de entrenamientos realizados */}
                        <Card sx={{ bgcolor: 'primary.main', color: 'primary.contrastText' }}>
                            <CardContent>
                                <Stack direction="row" spacing={2} sx={{ alignItems: 'center' }}>
                                    <FitnessCenterIcon fontSize="large" />
                                    <Box>
                                        <Typography variant="h4">{totalWorkouts}</Typography>
                                        <Typography variant="body2">Sesiones Totales</Typography>
                                    </Box>
                                </Stack>
                            </CardContent>
                        </Card>
                    </Grid>
                    <Grid size={{ xs: 12, sm: 4 }}>
                        <Card sx={{ bgcolor: 'secondary.main', color: 'secondary.contrastText' }}>
                            <CardContent>
                                <Stack direction="row" spacing={2} sx={{ alignItems: 'center' }}>
                                    <TimerIcon fontSize="large" />
                                    <Box>
                                        <Typography variant="h4">{totalTime} min</Typography>
                                        <Typography variant="body2">Tiempo Invertido</Typography>
                                    </Box>
                                </Stack>
                            </CardContent>
                        </Card>
                    </Grid>
                    <Grid size={{ xs: 12, sm: 4 }}>
                        <Card sx={{ bgcolor: 'success.main', color: 'success.contrastText' }}>
                            <CardContent>
                                <Stack direction="row" spacing={2} sx={{ alignItems: 'center' }}>
                                    <TrendingUpIcon fontSize="large" />
                                    <Box>
                                        <Typography variant="h4">{avgStress}</Typography>
                                        <Typography variant="body2">Nivel de Estrés Promedio</Typography>
                                    </Box>
                                </Stack>
                            </CardContent>
                        </Card>
                    </Grid>
                </Grid>
                {/* Contenedor principal para línea de tiempo y tabla de actividades */}
                <Grid container spacing={4}>
                    <Grid size={{ xs: 12, md: 5 }}>
                        <Paper sx={{ p: 3, height: '100%' }}>
                            <Typography variant="h6" gutterBottom>Línea de Tiempo</Typography>
                            <Divider sx={{ mb: 2 }} />
                            <Timeline position="right">
                                {progresses.slice(0, 5).map((p, index) => (
                                    <TimelineItem key={p.id}>
                                        <TimelineOppositeContent color="text.secondary" sx={{ fontSize: '0.8rem' }}>
                                            {new Date(p.performedAt).toLocaleDateString()}
                                        </TimelineOppositeContent>
                                        <TimelineSeparator>
                                            <TimelineDot color={index === 0 ? 'primary' : 'grey'} />
                                            {index < 4 && <TimelineConnector />}
                                        </TimelineSeparator>
                                        <TimelineContent>
                                            <Typography variant="subtitle2">{p.routineName}</Typography>
                                            <Typography variant="body2" color="text.secondary">
                                                {p.repetitions} reps | {p.time}
                                            </Typography>
                                        </TimelineContent>
                                    </TimelineItem>
                                ))}
                                {progresses.length === 0 && (
                                    <Typography variant="body2" color="text.secondary" align="center">
                                        No hay actividades recientes.
                                    </Typography>
                                )}
                            </Timeline>
                        </Paper>
                    </Grid>

                    <Grid size={{ xs: 12, md: 7 }}>
                        <TableContainer component={Paper} sx={{ p: 2 }}>
                            <Typography variant="h6" sx={{ p: 1 }}>Detalle de Actividades</Typography>
                            <Table sx={{ minWidth: 500 }} size="small">
                                <TableHead>
                                    <TableRow>
                                        <TableCell>Fecha</TableCell>
                                        <TableCell>Rutina</TableCell>
                                        <TableCell align="right">Reps</TableCell>
                                        <TableCell align="right">Tiempo</TableCell>
                                        <TableCell>Estado</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {progresses.map((row) => (
                                        <TableRow key={row.id} hover>
                                            <TableCell>{new Date(row.performedAt).toLocaleDateString()}</TableCell>
                                            <TableCell sx={{ fontWeight: 'medium' }}>{row.routineName}</TableCell>
                                            <TableCell align="right">{row.repetitions}</TableCell>
                                            <TableCell align="right">{row.time}</TableCell>
                                            <TableCell>
                                                <Box sx={{ 
                                                    px: 1, py: 0.5, borderRadius: 1, display: 'inline-block',
                                                    bgcolor: row.progressTypeName === 'EXCELLENT' ? 'success.light' : 'grey.200',
                                                    color: row.progressTypeName === 'EXCELLENT' ? 'success.dark' : 'text.primary',
                                                    fontSize: '0.75rem', fontWeight: 'bold'
                                                }}>
                                                    {row.progressTypeName}
                                                </Box>
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                    {progresses.length === 0 && (
                                        <TableRow>
                                            <TableCell colSpan={5} align="center" sx={{ py: 3 }}>
                                                Aún no has registrado ningún progreso. ¡Empieza hoy!
                                            </TableCell>
                                        </TableRow>
                                    )}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </Grid>
                </Grid>
            </Box>
        </Container>
    );
};

export default History;
