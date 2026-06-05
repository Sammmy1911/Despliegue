import { useCallback, useEffect, useState, type FormEvent } from 'react';
import {
    Alert,
    Box,
    Button,
    CircularProgress,
    FormControl,
    InputLabel,
    MenuItem,
    Paper,
    Select,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TextField,
    Typography,
} from '@mui/material';
import { getEvents, createEvent, updateEvent, deleteEvent } from '../../services/events.service';
import adminService from '../../services/admin.service';
import type { EventRequest, EventResponse } from '../../types/api';

type TrainerOption = {
    code: number;
    name?: string;
    email?: string;
};

type EventForm = {
    id?: number;
    name: string;
    description: string;
    managerId: number | null;
};

const initialForm: EventForm = {
    name: '',
    description: '',
    managerId: null,
};

export default function AdminEventsPage() {
    const [events, setEvents] = useState<EventResponse[]>([]);
    const [trainers, setTrainers] = useState<TrainerOption[]>([]);
    const [form, setForm] = useState<EventForm>(initialForm);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState<string>('');

    const load = useCallback(async () => {
        setError('');
        setLoading(true);

        try {
            const [eventsData, trainerData] = await Promise.all([getEvents(), adminService.getTrainers()]);
            setEvents(Array.isArray(eventsData) ? eventsData : []);
            setTrainers(Array.isArray(trainerData) ? trainerData : []);
        } catch {
            setError('No se pudo cargar la lista de eventos o entrenadores. Intenta nuevamente.');
            setEvents([]);
            setTrainers([]);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        const loadData = async () => {
            await load();
        };

        void loadData();
    }, [load]);

    const resetForm = () => {
        setForm(initialForm);
        setError('');
    };

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setError('');
        setSaving(true);

        if (!form.name.trim() || !form.description.trim()) {
            setError('El nombre y la descripción son obligatorios.');
            setSaving(false);
            return;
        }

        const payload: EventRequest = {
            name: form.name.trim(),
            description: form.description.trim(),
            managerId: form.managerId,
        };

        try {
            if (form.id !== undefined) {
                await updateEvent(form.id, payload);
            } else {
                await createEvent(payload);
            }

            resetForm();
            await load();
        } catch {
            setError('No se pudo guardar el evento. Verifica la conexión con el backend.');
        } finally {
            setSaving(false);
        }
    };

    const handleEdit = (eventData: EventResponse) => {
        setForm({
            id: eventData.id,
            name: eventData.name,
            description: eventData.description,
            managerId: eventData.managerCode,
        });
        setError('');
    };

    const handleDelete = async (id: number) => {
        setError('');
        setSaving(true);

        try {
            await deleteEvent(id);
            await load();
        } catch {
            setError('No se pudo eliminar el evento.');
        } finally {
            setSaving(false);
        }
    };

    return (
        <Box>
            <Typography variant="h4" gutterBottom>
                Administración de Eventos
            </Typography>

            {error ? (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            ) : null}

            {loading ? (
                <Box sx={{ display: 'grid', placeItems: 'center', minHeight: 220 }}>
                    <CircularProgress />
                </Box>
            ) : (
                <Box sx={{ display: 'grid', gap: 3 }}>
                    <Paper sx={{ p: 3 }}>
                        <Typography variant="h6" gutterBottom>
                            Crear o editar evento
                        </Typography>
                        <Box component="form" onSubmit={handleSubmit} sx={{ display: 'grid', gap: 2 }}>
                            <TextField
                                label="Nombre del evento"
                                value={form.name}
                                onChange={(event) => setForm((current) => ({ ...current, name: event.target.value }))}
                                fullWidth
                            />
                            <TextField
                                label="Descripción"
                                value={form.description}
                                onChange={(event) => setForm((current) => ({ ...current, description: event.target.value }))}
                                fullWidth
                                multiline
                                minRows={3}
                            />
                            <FormControl fullWidth>
                                <InputLabel id="event-manager-label">Entrenador responsable</InputLabel>
                                <Select
                                    labelId="event-manager-label"
                                    label="Entrenador responsable"
                                    value={form.managerId !== null ? String(form.managerId) : ''}
                                    onChange={(event) => {
                                        const value = event.target.value;
                                        setForm((current) => ({
                                            ...current,
                                            managerId: value === '' ? null : Number(value),
                                        }));
                                    }}
                                >
                                    <MenuItem value="">Sin entrenador</MenuItem>
                                    {trainers.map((trainer) => (
                                        <MenuItem key={trainer.code} value={trainer.code}>
                                            {trainer.name ?? trainer.email ?? `Entrenador ${trainer.code}`}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                            <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                                <Button type="submit" variant="contained" disabled={saving}>
                                    {form.id !== undefined ? 'Actualizar evento' : 'Crear evento'}
                                </Button>
                                <Button variant="outlined" onClick={resetForm} disabled={saving}>
                                    Limpiar formulario
                                </Button>
                            </Box>
                        </Box>
                    </Paper>

                    <Paper sx={{ p: 3 }}>
                        <Typography variant="h6" gutterBottom>
                            Eventos registrados
                        </Typography>
                        <TableContainer>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        <TableCell>Nombre</TableCell>
                                        <TableCell>Descripción</TableCell>
                                        <TableCell>Entrenador</TableCell>
                                        <TableCell align="right">Acciones</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {events.length === 0 ? (
                                        <TableRow>
                                            <TableCell colSpan={4}>
                                                No se encontraron eventos.
                                            </TableCell>
                                        </TableRow>
                                    ) : (
                                        events.map((eventData) => (
                                            <TableRow key={eventData.id}>
                                                <TableCell>{eventData.name}</TableCell>
                                                <TableCell>{eventData.description}</TableCell>
                                                <TableCell>{eventData.managerName ?? '---'}</TableCell>
                                                <TableCell align="right">
                                                    <Box sx={{ display: 'flex', gap: 1, justifyContent: 'flex-end' }}>
                                                        <Button size="small" onClick={() => handleEdit(eventData)}>
                                                            Editar
                                                        </Button>
                                                        <Button size="small" color="error" onClick={() => void handleDelete(eventData.id)}>
                                                            Eliminar
                                                        </Button>
                                                    </Box>
                                                </TableCell>
                                            </TableRow>
                                        ))
                                    )}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </Paper>
                </Box>
            )}
        </Box>
    );
}
