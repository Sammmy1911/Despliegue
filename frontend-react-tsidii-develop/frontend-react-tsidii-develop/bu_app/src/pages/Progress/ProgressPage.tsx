import {
    Alert,
    Box,
    Button,
    Card,
    CardContent,
    FormControl,
    InputLabel,
    MenuItem,
    Select,
    Stack,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
    TextField,
    Typography,
} from '@mui/material';
import axios from 'axios';
import { useCallback, useEffect, useMemo, useState, type FormEvent } from 'react';
import { useAuth } from '../../context/useAuth';
import { getAllRoutines, getTraineeRoutines, getTrainerRoutines } from '../../services/routines.service';
import {
    createProgress,
    getAggregatedProgress,
    getProgressTypes,
    getStressLevels,
    searchProgresses,
} from '../../services/progress.service';
import type { ProgressRequest, ProgressResponse, RoutineResponse, SelectOption } from '../../types/api';
import { normalizeRole } from '../../utils/roles';
import { toIsoLocalDateTime } from '../../utils/datetime';
import UserCodeAutocomplete, { type UserLookupOption } from '../../components/UserCodeAutocomplete';
import RoutineAutocomplete, { type RoutineLookupOption } from '../../components/RoutineAutocomplete';

type ApiErrorBody = {
    message?: string;
    error?: string;
    details?: string;
};

type ProgressForm = {
    repetitions: string;
    time: string;
    stressLevelId: string;
    progressTypeId: string;
    traineeId: string;
    routineId: string;
    performedAt: string;
};

const now = new Date();
const defaultFrom = new Date(now.getTime() - 1000 * 60 * 60 * 24 * 7).toISOString().slice(0, 10);
const defaultTo = now.toISOString().slice(0, 10);

const initialForm: ProgressForm = {
    repetitions: '',
    time: '',
    stressLevelId: '',
    progressTypeId: '',
    traineeId: '',
    routineId: '',
    performedAt: now.toISOString().slice(0, 16),
};

export default function ProgressPage() {
    const { user } = useAuth();
    const role = normalizeRole(user?.role);

    const [progresses, setProgresses] = useState<ProgressResponse[]>([]);
    const [routines, setRoutines] = useState<RoutineResponse[]>([]);
    const [stressLevels, setStressLevels] = useState<SelectOption[]>([]);
    const [progressTypes, setProgressTypes] = useState<SelectOption[]>([]);
    const [aggregated, setAggregated] = useState<{ period: string; totalRepetitions: number; entriesCount: number }[]>([]);
    const [period, setPeriod] = useState<'daily' | 'weekly'>('weekly');
    const [from, setFrom] = useState<string>(defaultFrom);
    const [to, setTo] = useState<string>(defaultTo);
    const [form, setForm] = useState<ProgressForm>(initialForm);
    const [error, setError] = useState<string | null>(null);

    const currentTraineeId = useMemo<number | undefined>(() => {
        if (role === 'TRAINEE') {
            return user?.code;
        }

        if (!form.traineeId) {
            return undefined;
        }

        return Number(form.traineeId);
    }, [form.traineeId, role, user?.code]);

    const routineOptions = useMemo<RoutineLookupOption[]>(() => {
        if (role !== 'TRAINEE' && !currentTraineeId) {
            return [];
        }

        return routines
            .filter((routine) => {
                if (!currentTraineeId) {
                    return true;
                }

                return routine.traineeCode === currentTraineeId;
            })
            .map((routine) => ({
                id: routine.id,
                name: routine.name,
                traineeName: routine.traineeName,
            }));
    }, [currentTraineeId, role, routines]);

    const effectiveRoutineId = useMemo(() => {
        if (!form.routineId) {
            return '';
        }

        const selectedRoutineId = Number(form.routineId);
        const isValid = routines.some((routine) => {
            if (routine.id !== selectedRoutineId) {
                return false;
            }

            if (!currentTraineeId) {
                return true;
            }

            return routine.traineeCode === currentTraineeId;
        });

        return isValid ? form.routineId : '';
    }, [currentTraineeId, form.routineId, routines]);

    const traineeOptions = useMemo<UserLookupOption[]>(() => {
        const map = new Map<number, UserLookupOption>();

        if (role === 'TRAINEE' && user?.code && user?.name) {
            map.set(user.code, {
                code: user.code,
                name: user.name,
            });
        }

        routines.forEach((routine) => {
            map.set(routine.traineeCode, {
                code: routine.traineeCode,
                name: routine.traineeName,
            });
        });

        return Array.from(map.values()).sort((a, b) => a.name.localeCompare(b.name));
    }, [role, routines, user]);

    const extractApiErrorMessage = (errorValue: unknown): string | null => {
        if (!axios.isAxiosError<ApiErrorBody>(errorValue)) {
            return null;
        }

        const status = errorValue.response?.status;
        const data = errorValue.response?.data;

        if (data?.message) {
            return data.message;
        }

        if (data?.details) {
            return data.details;
        }

        if (data?.error) {
            return data.error;
        }

        if (status === 403) {
            return 'No tienes permisos para registrar este progreso.';
        }

        if (status === 400) {
            return 'El backend rechazó los datos del progreso. Verifica trainee, rutina, tiempo y fecha.';
        }

        return null;
    };

    const toIsoLocalTimeFromMinutes = (minutesValue: string): string | null => {
        const totalMinutes = Number(minutesValue);

        if (!Number.isFinite(totalMinutes) || totalMinutes <= 0) {
            return null;
        }

        const safeMinutes = Math.floor(totalMinutes);
        const hours = Math.floor(safeMinutes / 60)
            .toString()
            .padStart(2, '0');
        const minutes = (safeMinutes % 60).toString().padStart(2, '0');

        return `${hours}:${minutes}:00`;
    };

    const fetchRoutines = useCallback(async () => {
        if (!user?.code) {
            return;
        }

        if (role === 'ADMIN') {
            setRoutines(await getAllRoutines());
            return;
        }

        if (role === 'TRAINER') {
            setRoutines(await getTrainerRoutines(user.code));
            return;
        }

        setRoutines(await getTraineeRoutines(user.code));
    }, [role, user]);

    const fetchProgressData = useCallback(async (traineeId?: number) => {
        if (!traineeId) {
            return;
        }

        const fromDateTime = toIsoLocalDateTime(from);
        const toDateTime = toIsoLocalDateTime(to, true);

        const [progressRows, aggregatedRows] = await Promise.all([
            searchProgresses(fromDateTime, toDateTime, traineeId),
            getAggregatedProgress(period, fromDateTime, toDateTime, traineeId),
        ]);

        setProgresses(progressRows);
        setAggregated(aggregatedRows);
    }, [from, period, to]);

    useEffect(() => {
        const load = async () => {
            try {
                setError(null);
                await Promise.all([
                    fetchRoutines(),
                    getStressLevels().then((response) => {
                        setStressLevels(response);
                    }),
                    getProgressTypes().then((response) => {
                        setProgressTypes(response);
                    }),
                ]);
            } catch {
                setError('No se pudieron cargar los catálogos de progreso.');
            }
        };

        void load();
    }, [fetchRoutines]);

    useEffect(() => {
        const traineeId = role === 'TRAINEE' ? user?.code : form.traineeId ? Number(form.traineeId) : undefined;

        const load = async () => {
            try {
                setError(null);
                await fetchProgressData(traineeId);
            } catch {
                setError('No se pudo consultar el progreso.');
            }
        };

        void load();
    }, [fetchProgressData, form.traineeId, role, user?.code]);

    const handleSaveProgress = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        if (
            !form.repetitions ||
            !form.time ||
            !form.stressLevelId ||
            !form.progressTypeId ||
            !currentTraineeId ||
            !effectiveRoutineId ||
            !form.performedAt
        ) {
            setError('Completa todos los campos para registrar progreso.');
            return;
        }

        try {
            setError(null);
            const normalizedTime = toIsoLocalTimeFromMinutes(form.time);

            if (!normalizedTime) {
                setError('El tiempo debe ser un número positivo de minutos.');
                return;
            }

            const payload: ProgressRequest = {
                repetitions: Number(form.repetitions),
                time: normalizedTime,
                stressLevelId: Number(form.stressLevelId),
                progressTypeId: Number(form.progressTypeId),
                traineeId: currentTraineeId,
                routineId: Number(effectiveRoutineId),
                performedAt: toIsoLocalDateTime(form.performedAt),
            };

            await createProgress(payload);
            await fetchProgressData(currentTraineeId);
        } catch (errorValue: unknown) {
            const apiMessage = extractApiErrorMessage(errorValue);
            setError(apiMessage ?? 'No fue posible guardar el progreso.');
        }
    };

    return (
        <Stack spacing={3}>
            <Typography variant="h4" sx={{ fontWeight: 700 }}>
                Seguimiento de progreso
            </Typography>

            {error ? <Alert severity="error">{error}</Alert> : null}

            <Card>
                <CardContent>
                    <Typography variant="h6" sx={{ mb: 2 }}>
                        Registrar progreso
                    </Typography>
                    <Box component="form" onSubmit={handleSaveProgress}>
                        <Stack spacing={2}>
                            <TextField
                                label="Repeticiones"
                                type="number"
                                value={form.repetitions}
                                onChange={(event) => {
                                    setForm((current) => ({ ...current, repetitions: event.target.value }));
                                }}
                            />
                            <TextField
                                label="Tiempo (minutos)"
                                type="number"
                                value={form.time}
                                onChange={(event) => {
                                    setForm((current) => ({ ...current, time: event.target.value }));
                                }}
                            />

                            <FormControl fullWidth>
                                <InputLabel id="stress-level-label">Nivel de esfuerzo</InputLabel>
                                <Select
                                    labelId="stress-level-label"
                                    label="Nivel de esfuerzo"
                                    value={form.stressLevelId}
                                    onChange={(event) => {
                                        setForm((current) => ({ ...current, stressLevelId: String(event.target.value) }));
                                    }}
                                >
                                    {stressLevels.map((option) => (
                                        <MenuItem key={option.id} value={String(option.id)}>
                                            {option.name}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>

                            <FormControl fullWidth>
                                <InputLabel id="progress-type-label">Tipo de progreso</InputLabel>
                                <Select
                                    labelId="progress-type-label"
                                    label="Tipo de progreso"
                                    value={form.progressTypeId}
                                    onChange={(event) => {
                                        setForm((current) => ({ ...current, progressTypeId: String(event.target.value) }));
                                    }}
                                >
                                    {progressTypes.map((option) => (
                                        <MenuItem key={option.id} value={String(option.id)}>
                                            {option.name}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>

                            <UserCodeAutocomplete
                                label="Trainee"
                                codeValue={currentTraineeId ? String(currentTraineeId) : ''}
                                options={traineeOptions}
                                disabled={role === 'TRAINEE'}
                                onCodeChange={(value) => {
                                    setForm((current) => ({ ...current, traineeId: value, routineId: '' }));
                                }}
                            />

                            <RoutineAutocomplete
                                label="Rutina"
                                value={effectiveRoutineId}
                                options={routineOptions}
                                disabled={role !== 'TRAINEE' && !currentTraineeId}
                                onChange={(value) => {
                                    setForm((current) => ({ ...current, routineId: value }));
                                }}
                            />

                            <TextField
                                label="Fecha y hora"
                                type="datetime-local"
                                value={form.performedAt}
                                onChange={(event) => {
                                    setForm((current) => ({ ...current, performedAt: event.target.value }));
                                }}
                                slotProps={{ inputLabel: { shrink: true } }}
                            />

                            <Button type="submit" variant="contained">
                                Guardar progreso
                            </Button>
                        </Stack>
                    </Box>
                </CardContent>
            </Card>

            <Card>
                <CardContent>
                    <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 2 }}>
                        <TextField
                            label="Desde"
                            type="date"
                            value={from}
                            onChange={(event) => {
                                setFrom(event.target.value);
                            }}
                            slotProps={{ inputLabel: { shrink: true } }}
                        />
                        <TextField
                            label="Hasta"
                            type="date"
                            value={to}
                            onChange={(event) => {
                                setTo(event.target.value);
                            }}
                            slotProps={{ inputLabel: { shrink: true } }}
                        />
                        <FormControl sx={{ minWidth: 200 }}>
                            <InputLabel id="period-label">Periodo agregado</InputLabel>
                            <Select
                                labelId="period-label"
                                value={period}
                                label="Periodo agregado"
                                onChange={(event) => {
                                    setPeriod(event.target.value as 'daily' | 'weekly');
                                }}
                            >
                                <MenuItem value="daily">Diario</MenuItem>
                                <MenuItem value="weekly">Semanal</MenuItem>
                            </Select>
                        </FormControl>
                    </Stack>

                    <Typography variant="h6" sx={{ mb: 2 }}>
                        Historial
                    </Typography>
                    <Table size="small">
                        <TableHead>
                            <TableRow>
                                <TableCell>Fecha</TableCell>
                                <TableCell>Rutina</TableCell>
                                <TableCell>Repeticiones</TableCell>
                                <TableCell>Tiempo</TableCell>
                                <TableCell>Esfuerzo</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {progresses.map((row) => (
                                <TableRow key={row.id}>
                                    <TableCell>{row.performedAt}</TableCell>
                                    <TableCell>{row.routineName}</TableCell>
                                    <TableCell>{row.repetitions}</TableCell>
                                    <TableCell>{row.time}</TableCell>
                                    <TableCell>{row.stressLevelName}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>

                    <Typography variant="h6" sx={{ mt: 3, mb: 1 }}>
                        Totales agregados
                    </Typography>
                    <Table size="small">
                        <TableHead>
                            <TableRow>
                                <TableCell>Periodo</TableCell>
                                <TableCell>Total repeticiones</TableCell>
                                <TableCell>Entradas</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {aggregated.map((row) => (
                                <TableRow key={row.period}>
                                    <TableCell>{row.period}</TableCell>
                                    <TableCell>{row.totalRepetitions}</TableCell>
                                    <TableCell>{row.entriesCount}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>
        </Stack>
    );
}
