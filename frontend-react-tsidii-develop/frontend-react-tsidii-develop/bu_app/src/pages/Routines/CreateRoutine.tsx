import React, { useEffect, useState } from 'react';
import {Container,Typography,Box,Paper,TextField,Button,List,ListItem,ListItemText,Checkbox,Divider,Alert,CircularProgress,Stack,
} from '@mui/material';
import { useAuth } from '../../context/useAuth';
import { routineService } from './services/routine.service';
import { exerciseService } from './services/exercise.service';
import type { ExerciseResponse } from './services/exercise.service';
import { routineExerciseService } from './services/routine-exercise.service';


type AuthUser = {
    code: number;
    role: string;
};
const CreateRoutine: React.FC = () => {
    const { user } = useAuth();

    const authUser = user as AuthUser | null;
    const trainerId = authUser?.code;

    const [routineName, setRoutineName] = useState('');
    const [exercises, setExercises] = useState<ExerciseResponse[]>([]);
    const [selectedExercises, setSelectedExercises] = useState<number[]>([]);
    const [loading, setLoading] = useState(false);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState(false);

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        setLoading(true);
        exerciseService.getAll()
            .then(data => {
                setExercises(data);
                setLoading(false);
            })
            .catch(() => {
                setError('Error al cargar la lista de ejercicios');
                setLoading(false);
            });
    }, []);

    const handleToggleExercise = (id: number) => {
        setSelectedExercises(prev =>
            prev.includes(id) ? prev.filter(e => e !== id) : [...prev, id]
        );
    };

    const handleCreateRoutine = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!routineName || selectedExercises.length === 0 || !trainerId) return;

        setSubmitting(true);
        setError(null);
        setSuccess(false);

        try {
            // 1. Create the base routine
            const newRoutine = await routineService.create({
                name: routineName,
                trainerCode: trainerId
            });

            // 2. Link each selected exercise
            const linkPromises = selectedExercises.map(exerciseId => 
                routineExerciseService.create({
                    routineId: newRoutine.id,
                    exerciseId: exerciseId
                })
            );

            await Promise.all(linkPromises);

            setSuccess(true);
            setRoutineName('');
            setSelectedExercises([]);
        } catch  {
            setError('Error al crear la rutina prediseñada');
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <Container maxWidth="md">
            <Box sx={{ my: 4 }}>
                <Typography variant="h4" component="h1" gutterBottom>
                    Subir Rutina Prediseñada
                </Typography>
                <Typography variant="body1" color="text.secondary" sx={{ mb: 2 }}>
                    Define el nombre de la rutina y selecciona los ejercicios que la componen.
                </Typography>

                {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
                {success && <Alert severity="success" sx={{ mb: 2 }}>Rutina creada exitosamente</Alert>}

                <Paper sx={{ p: 3, mb: 4 }}>
                    <form onSubmit={handleCreateRoutine}>
                        <Stack spacing={3}>
                            <TextField
                                label="Nombre de la Rutina"
                                variant="outlined"
                                fullWidth
                                required
                                value={routineName}
                                onChange={(e) => setRoutineName(e.target.value)}
                                placeholder="Ej: Full Body Iniciante"
                            />

                            <Typography variant="h6">Seleccionar Ejercicios</Typography>
                            
                            {loading ? (
                                <Box sx={{ display: 'flex', justifyContent: 'center', p: 2 }}>
                                    <CircularProgress size={24} />
                                </Box>
                            ) : (
                                <List sx={{ width: '100%', bgcolor: 'background.paper', maxHeight: 400, overflow: 'auto' }}>
                                    {exercises.map((exercise) => (
                                        <React.Fragment key={exercise.id}>
                                            <ListItem
                                                secondaryAction={
                                                    <Checkbox
                                                        edge="end"
                                                        onChange={() => handleToggleExercise(exercise.id)}
                                                        checked={selectedExercises.includes(exercise.id)}
                                                    />
                                                }
                                                disablePadding
                                            >
                                                <ListItemText
                                                    primary={exercise.name}
                                                    secondary={`${exercise.typeName} | ${exercise.difficultyName} | ${exercise.length} min`}
                                                    sx={{ px: 2 }}
                                                />
                                            </ListItem>
                                            <Divider />
                                        </React.Fragment>
                                    ))}
                                </List>
                            )}

                            <Button
                                type="submit"
                                variant="contained"
                                size="large"
                                disabled={submitting || !routineName || selectedExercises.length === 0}
                            >
                                {submitting ? 'Creando...' : 'Crear Rutina'}
                            </Button>
                        </Stack>
                    </form>
                </Paper>
            </Box>
        </Container>
    );
};

export default CreateRoutine;
