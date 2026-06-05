import React, { useEffect, useState, useMemo } from 'react';
import {Container,Typography,Box,FormControl,InputLabel,Select,MenuItem,Paper,Table,TableBody,TableCell,TableContainer,TableHead,TableRow,Button,Dialog,DialogTitle,DialogContent,DialogActions,TextField,CircularProgress,Alert,
} from '@mui/material';
import { useAuth } from '../../context/useAuth';
import { routineService } from './services/routine.service';
import type { RoutineResponse } from './services/routine.service';
import { progressService } from './services/progress.service';
import type { ProgressResponse } from './services/progress.service';
import { recommendationService } from './services/recommendation.service';
// import { TraineeResponse } from '../Trainees/services/trainee.service';
type AuthUser = {
    code: number;
    role: string;
};

const Recommendations: React.FC = () => {
    const { user } = useAuth();

    const authUser = user as AuthUser | null;

    const trainerId = authUser?.code;
    const userRole = authUser?.role;

    const canGenerateRecommendations = userRole === 'TRAINER';


    const [successMessage, setSuccessMessage] = useState<string | null>(null);
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    const [routines, setRoutines] = useState<RoutineResponse[]>([]); // Rutinas asociadas al entrenador para obtener los alumnos
    const [selectedTraineeId, setSelectedTraineeId] = useState<number | ''>(''); // ID del alumno seleccionado
    const [progresses, setProgresses] = useState<ProgressResponse[]>([]); // Progresos del alumno seleccionado
    const [loading, setLoading] = useState(false); // Para manejar el estado de carga
    const [error, setError] = useState<string | null>(null);

    // Dialog state
    const [openDialog, setOpenDialog] = useState(false);
    const [selectedProgress, setSelectedProgress] = useState<ProgressResponse | null>(null);
    const [recommendationText, setRecommendationText] = useState('');
    const [submitting, setSubmitting] = useState(false);
    
    useEffect(() => {
        if (trainerId && canGenerateRecommendations) {
            // eslint-disable-next-line react-hooks/set-state-in-effect
            setLoading(true);
            routineService.getByTrainer(trainerId)
                .then(data => {
                    setRoutines(data);
                    setLoading(false);
                })
                .catch(() =>  {
                    setError('Error al cargar alumnos');
                    setLoading(false);
                });
        }
    }, [trainerId, canGenerateRecommendations]);

    const trainees = useMemo(() => {
        const uniqueTrainees = new Map<number, string>();
        routines.forEach(r => {
            if (r.traineeCode && r.traineeName) {
                uniqueTrainees.set(r.traineeCode, r.traineeName);
            }
        });
        return Array.from(uniqueTrainees.entries()).map(([code, name]) => ({ code, name }));
    }, [routines]);

    useEffect(() => {
        if (selectedTraineeId) {
            // eslint-disable-next-line react-hooks/set-state-in-effect
            setLoading(true);
            progressService.getByTrainee(Number(selectedTraineeId))
                .then(data => {
                    setProgresses(data);
                    setLoading(false);
                })
                .catch(() => {
                    setError('Error al cargar progresos');
                    setLoading(false);
                });
        } else {
            setProgresses([]);
        }
    }, [selectedTraineeId]);

    const handleOpenDialog = (progress: ProgressResponse) => { // Abrir el diálogo y establecer el progreso seleccionado
        setSelectedProgress(progress);
        setOpenDialog(true);
    };

    const handleCloseDialog = () => {
        setOpenDialog(false);
        setSelectedProgress(null); // Limpiar el progreso seleccionado al cerrar el diálogo
        setRecommendationText('');
    };

    const handleSubmitRecommendation = async () => {
        if (!selectedProgress || !recommendationText || !trainerId) return;

        setSubmitting(true);
        try {
            await recommendationService.create({
                description: recommendationText,
                trainerId,
                progressId: selectedProgress.id,
            });
            handleCloseDialog();
            setSuccessMessage('Recomendación guardada con éxito');
        } catch {
            setErrorMessage('Error al guardar la recomendación');
        } finally {
            setSubmitting(false);
        }
    };
    //Mejora de diseño y experiencia de usuario, con mensajes claros y manejo de estados de carga y error.
    //  Además, se asegura que solo los entrenadores puedan acceder a esta sección y se
    //  muestra un mensaje de advertencia si un usuario sin permisos intenta acceder.

    //Esta buenisimos lo iconos, la vd tuve que buscar como hacerlos, pero me gustaria que se 
    // vieran mas grandes
    return (
        <Container maxWidth="lg">
            <Box sx={{ my: 4 }}>
                <Typography variant="h4" component="h1" gutterBottom>
                    Generar Recomendaciones
                </Typography>
                <Typography variant="body1" color="text.secondary" sx={{ mb: 2 }}>
                    Selecciona un alumno para ver su progreso y generar recomendaciones personalizadas.
                </Typography>

                {!canGenerateRecommendations && (
                    <Alert severity="warning" sx={{ mb: 2 }}>
                        Esta sección está disponible solo para entrenadores.
                    </Alert>
                )}

                {successMessage && <Alert severity="success" sx={{ mb: 2 }}>{successMessage}</Alert>}
                {errorMessage && <Alert severity="error" sx={{ mb: 2 }}>{errorMessage}</Alert>}
                {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

                {canGenerateRecommendations && <Box sx={{ minWidth: 120, mb: 4 }}>
                    <FormControl fullWidth>
                        <InputLabel id="select-trainee-label">Seleccionar Alumno</InputLabel>
                        <Select
                            labelId="select-trainee-label"
                            id="select-trainee"
                            value={selectedTraineeId}
                            label="Seleccionar Alumno"
                            onChange={(e) => setSelectedTraineeId(e.target.value as number)}
                        >
                            <MenuItem value="">
                                <em>Ninguno</em>
                            </MenuItem>
                            {trainees.map((t) => (
                                <MenuItem key={t.code} value={t.code}>
                                    {t.name} (ID: {t.code})
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </Box>}

                {canGenerateRecommendations && loading ? (
                    <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
                        <CircularProgress />
                    </Box>
                ) : (
                    <>
                        {selectedTraineeId && (
                            <TableContainer component={Paper}>
                                <Table sx={{ minWidth: 650 }} aria-label="tabla de progresos">
                                    <TableHead>
                                        <TableRow>
                                            <TableCell>Rutina</TableCell>
                                            <TableCell>Fecha</TableCell>
                                            <TableCell align="right">Repeticiones</TableCell>
                                            <TableCell align="right">Tiempo</TableCell>
                                            <TableCell>Estrés</TableCell>
                                            <TableCell>Estado</TableCell>
                                            <TableCell align="center">Acción</TableCell>
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {progresses.length === 0 ? (
                                            <TableRow>
                                                <TableCell colSpan={7} align="center">
                                                    No hay progresos registrados para este alumno.
                                                </TableCell>
                                            </TableRow>
                                        ) : (
                                            progresses.map((row) => (
                                                <TableRow key={row.id}>
                                                    <TableCell component="th" scope="row">
                                                        {row.routineName}
                                                    </TableCell>
                                                    <TableCell>{new Date(row.performedAt).toLocaleDateString()}</TableCell>
                                                    <TableCell align="right">{row.repetitions}</TableCell>
                                                    <TableCell align="right">{row.time}</TableCell>
                                                    <TableCell>{row.stressLevelName}</TableCell>
                                                    <TableCell>{row.progressTypeName}</TableCell>
                                                    <TableCell align="center">
                                                        <Button 
                                                            variant="contained" 
                                                            size="small"
                                                            onClick={() => handleOpenDialog(row)}
                                                        >
                                                            Recomendar
                                                        </Button>
                                                    </TableCell>
                                                </TableRow>
                                            ))
                                        )}
                                    </TableBody>
                                </Table>
                            </TableContainer>
                        )}
                    </>
                )}
            </Box>

            {/* Diálogo para Recomendación */}
            <Dialog open={openDialog} onClose={handleCloseDialog} fullWidth maxWidth="sm">
                <DialogTitle>Nueva Recomendación</DialogTitle>
                <DialogContent>
                    <Typography variant="subtitle2" gutterBottom>
                        Progreso del {selectedProgress && new Date(selectedProgress.performedAt).toLocaleDateString()} - {selectedProgress?.routineName}
                    </Typography>
                    <TextField
                        autoFocus
                        margin="dense"
                        id="recommendation"
                        label="Descripción de la recomendación"
                        type="text"
                        fullWidth
                        multiline
                        rows={4}
                        variant="outlined"
                        value={recommendationText}
                        onChange={(e) => setRecommendationText(e.target.value)}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDialog} disabled={submitting}>Cancelar</Button>
                    <Button 
                        onClick={handleSubmitRecommendation} 
                        variant="contained" 
                        disabled={submitting || !recommendationText}
                    >
                        {submitting ? 'Guardando...' : 'Guardar'}
                    </Button>
                </DialogActions>
            </Dialog>
        </Container>
    );
};
// Mejora de diseño y experiencia de usuario, con mensajes claros y manejo de estados de carga y error.
//  Además, se asegura que solo los entrenadores puedan acceder a esta sección y se
//  muestra un mensaje de advertencia si un usuario sin permisos intenta acceder.

export default Recommendations;
