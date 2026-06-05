import { Alert, Button, Card, CardContent, Grid, Stack, Table, TableBody, TableCell, TableHead, TableRow, Typography, TextField } from '@mui/material';
import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/useAuth';
import { searchProgresses } from '../../services/progress.service';
import { getTrainerRoutines } from '../../services/routines.service';
import type { ProgressResponse, RoutineResponse } from '../../types/api';
import { normalizeRole } from '../../utils/roles';
import { toIsoLocalDateTime } from '../../utils/datetime';
import alertsService from '../../services/alerts.service';

export default function TrainerDashboard() {
    const { user } = useAuth();
    const role = normalizeRole(user?.role);
    const navigate = useNavigate();

    const [routines, setRoutines] = useState<RoutineResponse[]>([]);
    const [progresses, setProgresses] = useState<ProgressResponse[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [progressWarning, setProgressWarning] = useState<string | null>(null);

    const traineeCodes = useMemo(() => {
        return Array.from(new Set(routines.map((routine) => routine.traineeCode)));
    }, [routines]);

    useEffect(() => {
        const load = async () => {
            if (!user?.code || (role !== 'TRAINER' && role !== 'ADMIN')) {
                return;
            }

            try {
                setError(null);
                setProgressWarning(null);
                const routinesData = await getTrainerRoutines(user.code);
                setRoutines(routinesData);

                const today = new Date().toISOString().slice(0, 10);
                const from = new Date(Date.now() - 1000 * 60 * 60 * 24 * 30).toISOString().slice(0, 10);
                const fromDateTime = toIsoLocalDateTime(from);
                const toDateTime = toIsoLocalDateTime(today, true);

                const traineeCodes = Array.from(new Set(routinesData.map((routine) => routine.traineeCode)));

                const progressData = await Promise.allSettled(
                    traineeCodes.map((traineeCode) => searchProgresses(fromDateTime, toDateTime, traineeCode)),
                );

                const successfulResults = progressData
                    .filter((result): result is PromiseFulfilledResult<ProgressResponse[]> => result.status === 'fulfilled')
                    .map((result) => result.value)
                    .flat();

                const failedResults = progressData.filter((result) => result.status === 'rejected');

                if (failedResults.length > 0) {
                    setProgressWarning(
                        `No se pudo consultar el progreso de ${failedResults.length} trainee(s), pero se cargó la información disponible.`,
                    );
                }

                setProgresses(successfulResults);
            } catch {
                setError('No fue posible cargar el panel del entrenador.');
            }
        };

        void load();
    }, [role, user?.code]);

    if (role !== 'TRAINER' && role !== 'ADMIN') {
        return <Alert severity="warning">Solo entrenadores y administradores pueden ver este panel.</Alert>;
    }

    return (
        <Stack spacing={3}>
            <Typography variant="h4" sx={{ fontWeight: 700 }}>
                Panel del entrenador
            </Typography>

            {error && <Alert severity="error">{error}</Alert>}
            {progressWarning && <Alert severity="warning">{progressWarning}</Alert>}

            {/* Botones de navegación - DEBERÍAN APARECER AQUÍ */}
            <Grid container spacing={2}>
                <Grid>
                    <Button
                        variant="contained"
                        onClick={() => navigate('/app/routines/new')}
                    >
                        Crear Nueva Rutina
                    </Button>
                </Grid>
                <Grid>
                    <Button
                        variant="outlined"
                        onClick={() => navigate('/app/recommendations')}
                    >
                        Ver Recomendaciones
                    </Button>
                </Grid>
            </Grid>

            <Card>
                <CardContent>
                    <Typography variant="h6" sx={{ mb: 2 }}>
                        Trainees asignados ({traineeCodes.length})
                    </Typography>
                    <Stack direction="row" spacing={1.5} sx={{ flexWrap: 'wrap' }}>
                        {traineeCodes.map((code) => (
                            <Typography key={code} sx={{ px: 1.5, py: 0.5, bgcolor: 'grey.200', borderRadius: 2 }}>
                                {code}
                            </Typography>
                        ))}
                    </Stack>
                </CardContent>
            </Card>

            <Card>
                <CardContent>
                    <Typography variant="h6" sx={{ mb: 2 }}>
                        Rutinas asignadas
                    </Typography>
                    <Table size="small">
                        <TableHead>
                            <TableRow>
                                <TableCell>Rutina</TableCell>
                                <TableCell>Trainee</TableCell>
                                <TableCell>Entrenador</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {routines.map((routine) => (
                                <TableRow key={routine.id}>
                                    <TableCell>{routine.name}</TableCell>
                                    <TableCell>{routine.traineeName}</TableCell>
                                    <TableCell>{routine.trainerName}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>

            <Card>
                <CardContent>
                    <Typography variant="h6" sx={{ mb: 2 }}>
                        Progreso reciente (30 días)
                    </Typography>
                    <Table size="small">
                        <TableHead>
                            <TableRow>
                                <TableCell>Trainee</TableCell>
                                <TableCell>Rutina</TableCell>
                                <TableCell>Fecha</TableCell>
                                <TableCell>Repeticiones</TableCell>
                                <TableCell>Tiempo</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {progresses.map((progress) => (
                                <TableRow key={progress.id}>
                                    <TableCell>{progress.traineeName}</TableCell>
                                    <TableCell>{progress.routineName}</TableCell>
                                    <TableCell>{progress.performedAt}</TableCell>
                                    <TableCell>{progress.repetitions}</TableCell>
                                    <TableCell>{progress.time}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>

            <Card>
                <CardContent>
                    <Typography variant="h6" sx={{ mb: 2 }}>
                        Enviar alerta a un trainee
                    </Typography>
                    <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                        <TextField label="Trainee code" size="small" id="traineeCode" />
                        <TextField label="Mensaje" size="small" id="alertMessage" sx={{ flex: 1 }} />
                        <Button
                            variant="contained"
                            onClick={async () => {
                                const traineeCode = (document.getElementById('traineeCode') as HTMLInputElement)?.value;
                                const message = (document.getElementById('alertMessage') as HTMLInputElement)?.value;

                                if (!traineeCode || !message || !user?.code) return;

                                try {
                                    await alertsService.createAlert({
                                        message,
                                        trainerId: user.code,
                                        traineeId: Number(traineeCode),
                                    });
                                    // Limpiar campos después de enviar
                                    (document.getElementById('traineeCode') as HTMLInputElement).value = '';
                                    (document.getElementById('alertMessage') as HTMLInputElement).value = '';
                                } catch {
                                    // ignore
                                }
                            }}
                        >
                            Enviar
                        </Button>
                    </Stack>
                </CardContent>
            </Card>
        </Stack>
    );
}