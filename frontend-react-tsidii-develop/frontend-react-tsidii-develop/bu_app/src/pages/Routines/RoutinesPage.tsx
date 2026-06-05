import {
    Alert,
    Box,
    Button,
    Card,
    CardContent,
    Stack,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
    TextField,
    Typography,
} from '@mui/material';
import { useCallback, useEffect, useMemo, useState, type FormEvent } from 'react';
import {
    createRoutine,
    createRoutineExercise,
    deleteRoutine,
    getAllRoutines,
    getTraineeRoutines,
    getTrainerRoutines,
    updateRoutine,
} from '../../services/routines.service';
import { getExercises } from '../../services/exercises.service';
import { useAuth } from '../../context/useAuth';
import type { ExerciseResponse, RoutineRequest, RoutineResponse } from '../../types/api';
import { normalizeRole } from '../../utils/roles';
import UserCodeAutocomplete, { type UserLookupOption } from '../../components/UserCodeAutocomplete';
import RoutineAutocomplete, { type RoutineLookupOption } from '../../components/RoutineAutocomplete';
import ExerciseAutocomplete, { type ExerciseLookupOption } from '../../components/ExerciseAutocomplete';

type RoutineForm = {
    id: number | null;
    name: string;
    trainerCode: string;
    traineeCode: string;
};

const initialForm: RoutineForm = {
    id: null,
    name: '',
    trainerCode: '',
    traineeCode: '',
};

export default function RoutinesPage() {
    const { user } = useAuth();
    const role = normalizeRole(user?.role);

    const [rows, setRows] = useState<RoutineResponse[]>([]);
    const [exercises, setExercises] = useState<ExerciseResponse[]>([]);
    const [form, setForm] = useState<RoutineForm>(initialForm);
    const [linkTraineeCode, setLinkTraineeCode] = useState<string>('');
    const [selectedExerciseId, setSelectedExerciseId] = useState<string>('');
    const [selectedRoutineId, setSelectedRoutineId] = useState<string>('');
    const [error, setError] = useState<string | null>(null);

    const exerciseOptions = useMemo<ExerciseLookupOption[]>(() => {
        return exercises.map((exercise) => ({
            id: exercise.id,
            name: exercise.name,
            typeName: exercise.typeName,
            difficultyName: exercise.difficultyName,
        }));
    }, [exercises]);

    const trainerOptions = useMemo<UserLookupOption[]>(() => {
        const map = new Map<number, UserLookupOption>();

        if (user?.code && user?.name) {
            map.set(user.code, {
                code: user.code,
                name: user.name,
            });
        }

        rows.forEach((routine) => {
            map.set(routine.trainerCode, {
                code: routine.trainerCode,
                name: routine.trainerName,
            });
        });

        return Array.from(map.values()).sort((a, b) => a.name.localeCompare(b.name));
    }, [rows, user]);

    const effectiveTrainerCode = useMemo(() => {
        if (role === 'TRAINER') {
            return user?.code ? String(user.code) : '';
        }

        return form.trainerCode;
    }, [form.trainerCode, role, user]);

    const traineeOptions = useMemo<UserLookupOption[]>(() => {
        const map = new Map<number, UserLookupOption>();

        rows.forEach((routine) => {
            if (!effectiveTrainerCode || routine.trainerCode === Number(effectiveTrainerCode)) {
                map.set(routine.traineeCode, {
                    code: routine.traineeCode,
                    name: routine.traineeName,
                });
            }
        });

        return Array.from(map.values()).sort((a, b) => a.name.localeCompare(b.name));
    }, [effectiveTrainerCode, rows]);

    const effectiveLinkTraineeCode = useMemo(() => {
        if (role === 'TRAINEE') {
            return user?.code ? String(user.code) : '';
        }

        return linkTraineeCode;
    }, [linkTraineeCode, role, user]);

    const routineOptions = useMemo<RoutineLookupOption[]>(() => {
        if (role !== 'TRAINEE' && !effectiveLinkTraineeCode) {
            return [];
        }

        return rows
            .filter((routine) => {
                if (!effectiveLinkTraineeCode) {
                    return true;
                }

                return routine.traineeCode === Number(effectiveLinkTraineeCode);
            })
            .map((routine) => ({
                id: routine.id,
                name: routine.name,
                traineeName: routine.traineeName,
            }));
    }, [effectiveLinkTraineeCode, role, rows]);

    const effectiveSelectedRoutineId = useMemo(() => {
        if (!selectedRoutineId) {
            return '';
        }

        const isValid = routineOptions.some((routine) => String(routine.id) === selectedRoutineId);

        return isValid ? selectedRoutineId : '';
    }, [routineOptions, selectedRoutineId]);

    const fetchRoutines = useCallback(async () => {
        if (!user?.code) {
            return;
        }

        if (role === 'ADMIN') {
            setRows(await getAllRoutines());
            return;
        }

        if (role === 'TRAINER') {
            setRows(await getTrainerRoutines(user.code));
            return;
        }

        setRows(await getTraineeRoutines(user.code));
    }, [role, user]);

    useEffect(() => {
        const load = async () => {
            try {
                setError(null);
                await Promise.all([
                    fetchRoutines(),
                    getExercises().then((response) => {
                        setExercises(response);
                    }),
                ]);
            } catch {
                setError('No se pudieron cargar las rutinas.');
            }
        };

        void load();
    }, [fetchRoutines]);

    const handleSaveRoutine = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        if (!form.name || !effectiveTrainerCode || !form.traineeCode) {
            setError('Completa nombre, entrenador y trainee.');
            return;
        }

        try {
            setError(null);
            const payload: RoutineRequest = {
                name: form.name,
                trainerCode: Number(effectiveTrainerCode),
                traineeCode: Number(form.traineeCode),
            };

            if (form.id) {
                await updateRoutine(form.id, payload);
            } else {
                await createRoutine(payload);
            }

            setForm(initialForm);
            await fetchRoutines();
        } catch {
            setError('No fue posible guardar la rutina.');
        }
    };

    const handleDeleteRoutine = async (id: number) => {
        try {
            setError(null);
            await deleteRoutine(id);
            await fetchRoutines();
        } catch {
            setError('No fue posible eliminar la rutina.');
        }
    };

    const handleLinkExercise = async () => {
        if (!selectedExerciseId || !effectiveSelectedRoutineId) {
            setError('Selecciona rutina y ejercicio para vincular.');
            return;
        }

        try {
            setError(null);
            await createRoutineExercise({
                routineId: Number(effectiveSelectedRoutineId),
                exerciseId: Number(selectedExerciseId),
            });
        } catch {
            setError('No fue posible vincular el ejercicio a la rutina.');
        }
    };

    return (
        <Stack spacing={3}>
            <Typography variant="h4" sx={{ fontWeight: 700 }}>
                Gestión de rutinas
            </Typography>

            {error ? <Alert severity="error">{error}</Alert> : null}

            <Card>
                <CardContent>
                    <Typography variant="h6" sx={{ mb: 2 }}>
                        {form.id ? 'Editar rutina' : 'Crear rutina'}
                    </Typography>
                    <Box component="form" onSubmit={handleSaveRoutine}>
                        <Stack spacing={2}>
                            <TextField
                                label="Nombre de la rutina"
                                value={form.name}
                                onChange={(event) => {
                                    setForm((current) => ({ ...current, name: event.target.value }));
                                }}
                            />

                            <UserCodeAutocomplete
                                label="Entrenador"
                                codeValue={effectiveTrainerCode}
                                options={trainerOptions}
                                disabled={role === 'TRAINER'}
                                onCodeChange={(value) => {
                                    setForm((current) => ({ ...current, trainerCode: value, traineeCode: '' }));
                                }}
                            />

                            <UserCodeAutocomplete
                                label="Trainee"
                                codeValue={form.traineeCode}
                                options={traineeOptions}
                                onCodeChange={(value) => {
                                    setForm((current) => ({ ...current, traineeCode: value }));
                                }}
                            />

                            <Stack direction="row" spacing={2}>
                                <Button type="submit" variant="contained">
                                    {form.id ? 'Actualizar' : 'Guardar'}
                                </Button>
                                <Button
                                    onClick={() => {
                                        setForm(initialForm);
                                    }}
                                >
                                    Limpiar
                                </Button>
                            </Stack>
                        </Stack>
                    </Box>
                </CardContent>
            </Card>

            <Card>
                <CardContent>
                    <Typography variant="h6" sx={{ mb: 2 }}>
                        Vincular ejercicio a rutina
                    </Typography>
                    <Stack
                        direction={{ xs: 'column', lg: 'row' }}
                        spacing={2}
                        sx={{
                            alignItems: { lg: 'center' },
                        }}
                    >
                        <Box sx={{ flex: 1, minWidth: { lg: 280 } }}>
                            <UserCodeAutocomplete
                                label="Trainee"
                                codeValue={effectiveLinkTraineeCode}
                                options={traineeOptions}
                                disabled={role === 'TRAINEE'}
                                onCodeChange={(value) => {
                                    setLinkTraineeCode(value);
                                    setSelectedRoutineId('');
                                }}
                            />
                        </Box>

                        <Box sx={{ flex: 1, minWidth: { lg: 280 } }}>
                            <RoutineAutocomplete
                                label="Rutina"
                                value={effectiveSelectedRoutineId}
                                options={routineOptions}
                                disabled={role !== 'TRAINEE' && !effectiveLinkTraineeCode}
                                onChange={(value) => {
                                    setSelectedRoutineId(value);
                                }}
                            />
                        </Box>

                        <Box sx={{ flex: 1, minWidth: { lg: 280 } }}>
                            <ExerciseAutocomplete
                                label="Ejercicio"
                                value={selectedExerciseId}
                                options={exerciseOptions}
                                onChange={(value) => {
                                    setSelectedExerciseId(value);
                                }}
                            />
                        </Box>

                        <Button
                            variant="contained"
                            onClick={() => void handleLinkExercise()}
                            sx={{
                                minWidth: { xs: '100%', lg: 160 },
                                height: 56,
                            }}
                        >
                            Vincular
                        </Button>
                    </Stack>
                </CardContent>
            </Card>

            <Card>
                <CardContent>
                    <Typography variant="h6" sx={{ mb: 2 }}>
                        Rutinas
                    </Typography>
                    <Table size="small">
                        <TableHead>
                            <TableRow>
                                <TableCell>Nombre</TableCell>
                                <TableCell>Entrenador</TableCell>
                                <TableCell>Trainee</TableCell>
                                <TableCell align="right">Acciones</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {rows.map((row) => (
                                <TableRow key={row.id}>
                                    <TableCell>{row.name}</TableCell>
                                    <TableCell>{row.trainerName}</TableCell>
                                    <TableCell>{row.traineeName}</TableCell>
                                    <TableCell align="right">
                                        <Stack direction="row" spacing={1} sx={{ justifyContent: 'flex-end' }}>
                                            <Button
                                                size="small"
                                                onClick={() => {
                                                    setForm({
                                                        id: row.id,
                                                        name: row.name,
                                                        trainerCode: String(row.trainerCode),
                                                        traineeCode: String(row.traineeCode),
                                                    });
                                                }}
                                            >
                                                Editar
                                            </Button>
                                            <Button
                                                size="small"
                                                color="error"
                                                onClick={() => {
                                                    void handleDeleteRoutine(row.id);
                                                }}
                                            >
                                                Eliminar
                                            </Button>
                                        </Stack>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>
        </Stack>
    );
}
