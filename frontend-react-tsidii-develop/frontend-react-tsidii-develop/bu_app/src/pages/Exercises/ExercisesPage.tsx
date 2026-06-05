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
    Switch,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
    TextField,
    Typography,
} from '@mui/material';
import axios from 'axios';
import { useEffect, useMemo, useState, type ChangeEvent, type FormEvent } from 'react';
import {
    createExercise,
    deleteExercise,
    getExerciseDifficulties,
    getExercises,
    getExerciseTypes,
    updateExercise,
} from '../../services/exercises.service';
import { useAuth } from '../../context/useAuth';
import type { ExerciseRequest, ExerciseResponse, SelectOption } from '../../types/api';
import { normalizeRole } from '../../utils/roles';

type ApiErrorBody = {
    message?: string;
    error?: string;
    details?: string;
};

type ExerciseForm = {
    id: number | null;
    name: string;
    length: string;
    description: string;
    custom: boolean;
    difficultyId: string;
    typeId: string;
};

const initialForm: ExerciseForm = {
    id: null,
    name: '',
    length: '',
    description: '',
    custom: false,
    difficultyId: '',
    typeId: '',
};

export default function ExercisesPage() {
    const { user } = useAuth();
    const role = normalizeRole(user?.role);
    const canCreateGlobalExercises = role === 'ADMIN';
    const [rows, setRows] = useState<ExerciseResponse[]>([]);
    const [types, setTypes] = useState<SelectOption[]>([]);
    const [difficulties, setDifficulties] = useState<SelectOption[]>([]);
    const [filter, setFilter] = useState<'all' | 'global' | 'custom'>('all');
    const [form, setForm] = useState<ExerciseForm>(initialForm);
    const [error, setError] = useState<string | null>(null);

    const effectiveCustom = canCreateGlobalExercises ? form.custom : true;

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
            return 'No tienes permisos para crear/editar este ejercicio.';
        }

        if (status === 400) {
            return 'El backend rechazó los datos del ejercicio. Verifica campos como descripción, tipo y dificultad.';
        }

        return null;
    };

    const filteredRows = useMemo(() => {
        if (filter === 'global') {
            return rows.filter((item) => !item.custom);
        }

        if (filter === 'custom') {
            return rows.filter((item) => item.custom);
        }

        return rows;
    }, [filter, rows]);

    const fetchData = async () => {
        const [exercisesData, typesData, difficultiesData] = await Promise.all([
            getExercises(),
            getExerciseTypes(),
            getExerciseDifficulties(),
        ]);

        setRows(exercisesData);
        setTypes(typesData);
        setDifficulties(difficultiesData);
    };

    useEffect(() => {
        const load = async () => {
            try {
                setError(null);
                await fetchData();
            } catch {
                setError('No se pudieron cargar los ejercicios o catálogos.');
            }
        };

        void load();
    }, []);

    const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
        const { name, value } = event.target;
        setForm((current) => ({ ...current, [name]: value }));
    };

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        if (!form.name || !form.length || !form.description || !form.difficultyId || !form.typeId) {
            setError('Completa todos los campos del formulario.');
            return;
        }

        if (!effectiveCustom && !canCreateGlobalExercises) {
            setError('Solo un administrador puede crear ejercicios globales. Activa "Ejercicio personalizado".');
            return;
        }

        if (effectiveCustom && !user?.code) {
            setError('No se pudo identificar el usuario para crear un ejercicio personalizado.');
            return;
        }

        try {
            setError(null);
            const payload: ExerciseRequest = {
                name: form.name,
                length: Number(form.length),
                description: form.description,
                custom: effectiveCustom,
                difficultyId: Number(form.difficultyId),
                typeId: Number(form.typeId),
                ownerCode: effectiveCustom ? user?.code : undefined,
            };

            if (form.id) {
                await updateExercise(form.id, payload);
            } else {
                await createExercise(payload);
            }

            setForm(initialForm);
            await fetchData();
        } catch (errorValue: unknown) {
            const apiMessage = extractApiErrorMessage(errorValue);
            setError(apiMessage ?? 'No fue posible guardar el ejercicio. Revisa los datos e inténtalo otra vez.');
        }
    };

    const handleEdit = (row: ExerciseResponse) => {
        setForm({
            id: row.id,
            name: row.name,
            length: String(row.length),
            description: row.description,
            custom: row.custom,
            difficultyId: String(row.difficultyId),
            typeId: String(row.typeId),
        });
    };

    const handleDelete = async (id: number) => {
        try {
            setError(null);
            await deleteExercise(id);
            await fetchData();
        } catch {
            setError('No fue posible eliminar el ejercicio.');
        }
    };

    return (
        <Stack spacing={3}>
            <Typography variant="h4" sx={{ fontWeight: 700 }}>
                Gestión de ejercicios
            </Typography>

            {error ? <Alert severity="error">{error}</Alert> : null}

            <Card>
                <CardContent>
                    <Typography variant="h6" sx={{ mb: 2 }}>
                        {form.id ? 'Editar ejercicio' : 'Crear ejercicio'}
                    </Typography>

                    <Box component="form" onSubmit={handleSubmit}>
                        <Stack spacing={2}>
                            <TextField label="Nombre" name="name" value={form.name} onChange={handleChange} fullWidth />
                            <TextField
                                label="Duración (minutos)"
                                name="length"
                                type="number"
                                value={form.length}
                                onChange={handleChange}
                                fullWidth
                            />
                            <TextField
                                label="Descripción"
                                name="description"
                                value={form.description}
                                onChange={handleChange}
                                multiline
                                minRows={3}
                                fullWidth
                            />

                            <FormControl fullWidth>
                                <InputLabel id="difficulty-select-label">Dificultad</InputLabel>
                                <Select
                                    labelId="difficulty-select-label"
                                    label="Dificultad"
                                    value={form.difficultyId}
                                    onChange={(event) => {
                                        setForm((current) => ({ ...current, difficultyId: String(event.target.value) }));
                                    }}
                                >
                                    {difficulties.map((option) => (
                                        <MenuItem key={option.id} value={String(option.id)}>
                                            {option.name}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>

                            <FormControl fullWidth>
                                <InputLabel id="type-select-label">Tipo</InputLabel>
                                <Select
                                    labelId="type-select-label"
                                    label="Tipo"
                                    value={form.typeId}
                                    onChange={(event) => {
                                        setForm((current) => ({ ...current, typeId: String(event.target.value) }));
                                    }}
                                >
                                    {types.map((option) => (
                                        <MenuItem key={option.id} value={String(option.id)}>
                                            {option.name}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>

                            <Stack direction="row" spacing={1} sx={{ alignItems: 'center' }}>
                                <Typography>Ejercicio global</Typography>
                                <Switch
                                    checked={effectiveCustom}
                                    disabled={!canCreateGlobalExercises}
                                    onChange={(event) => {
                                        setForm((current) => ({ ...current, custom: event.target.checked }));
                                    }}
                                />
                                <Typography>Ejercicio personalizado</Typography>
                            </Stack>
                            {!canCreateGlobalExercises ? (
                                <Typography variant="caption" color="text.secondary">
                                    Tu rol solo permite crear ejercicios personalizados.
                                </Typography>
                            ) : null}

                            <Stack direction="row" spacing={2}>
                                <Button type="submit" variant="contained">
                                    {form.id ? 'Actualizar' : 'Guardar'}
                                </Button>
                                <Button
                                    variant="text"
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
                    <Stack direction="row" spacing={2} sx={{ alignItems: 'center', mb: 2 }}>
                        <Typography variant="h6">Listado</Typography>
                        <FormControl size="small" sx={{ minWidth: 220 }}>
                            <InputLabel id="filter-label">Filtro</InputLabel>
                            <Select
                                labelId="filter-label"
                                value={filter}
                                label="Filtro"
                                onChange={(event) => {
                                    setFilter(event.target.value as 'all' | 'global' | 'custom');
                                }}
                            >
                                <MenuItem value="all">Todos</MenuItem>
                                <MenuItem value="global">Globales</MenuItem>
                                <MenuItem value="custom">Personalizados</MenuItem>
                            </Select>
                        </FormControl>
                    </Stack>

                    <Table size="small">
                        <TableHead>
                            <TableRow>
                                <TableCell>Nombre</TableCell>
                                <TableCell>Tipo</TableCell>
                                <TableCell>Dificultad</TableCell>
                                <TableCell>Duración</TableCell>
                                <TableCell>Propietario</TableCell>
                                <TableCell align="right">Acciones</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {filteredRows.map((row) => (
                                <TableRow key={row.id}>
                                    <TableCell>{row.name}</TableCell>
                                    <TableCell>{row.typeName}</TableCell>
                                    <TableCell>{row.difficultyName}</TableCell>
                                    <TableCell>{row.length} min</TableCell>
                                    <TableCell>{row.ownerName ?? 'Global'}</TableCell>
                                    <TableCell align="right">
                                        <Stack direction="row" spacing={1} sx={{ justifyContent: 'flex-end' }}>
                                            <Button size="small" onClick={() => handleEdit(row)}>
                                                Editar
                                            </Button>
                                            <Button
                                                size="small"
                                                color="error"
                                                onClick={() => {
                                                    void handleDelete(row.id);
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
