import { useCallback, useEffect, useState } from 'react';
import {
    Alert,
    Box,
    Button,
    CircularProgress,
    MenuItem,
    Paper,
    Select,
    Typography,
    List,
    ListItem,
    ListItemText,
} from '@mui/material';
import type { SelectChangeEvent } from '@mui/material/Select';
import { useNavigate } from 'react-router-dom';
import adminService from '../../services/admin.service';

type TrainerResponse = {
    code: number;
    id?: number;
    name?: string;
    email?: string;
};

type UserResponse = {
    code: number;
    id?: number;
    name?: string;
    email?: string;
    role?: {
        type?: string;
    };
    trainer?: {
        code?: number;
        id?: number;
    } | null;
};

export default function AdminPanel() {
    const [trainers, setTrainers] = useState<TrainerResponse[]>([]);
    const [users, setUsers] = useState<UserResponse[]>([]);
    const [assignments, setAssignments] = useState<Record<number, string | number>>({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string>('');
    const navigate = useNavigate();

    const trainees = users.filter((user) => user.role?.type?.toUpperCase() === 'TRAINEE');

    const load = useCallback(async () => {
        setError('');
        setLoading(true);

        try {
            const [t, u] = await Promise.all([adminService.getTrainers(), adminService.getUsers(0, 200)]);
            const trainerList = (Array.isArray(t) ? t : []) as TrainerResponse[];
            const userList = Array.isArray(u) ? (u as UserResponse[]) : (u?.content ?? []) as UserResponse[];

            setTrainers(trainerList);
            setUsers(userList);

            const map: Record<number, string | number> = {};
            userList.forEach((user) => {
                map[user.code] = user.trainer?.code ?? user.trainer?.id ?? '';
            });
            setAssignments(map);
        } catch {
            setError('No fue posible cargar los datos administrativos. Intenta de nuevo más tarde.');
            setTrainers([]);
            setUsers([]);
            setAssignments({});
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

    async function handleAssign(userId: number, trainerId: number | null) {
        setError('');

        try {
            await adminService.assignTrainerToUser(userId, trainerId);
            setAssignments((s) => ({ ...s, [userId]: trainerId ?? '' }));
        } catch {
            setError('No se pudo asignar el entrenador. Por favor revisa la configuración del backend e intenta de nuevo.');
        }
    }

    return (
        <Box>
            <Typography variant="h4" gutterBottom>
                Panel Administrativo
            </Typography>

            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mb: 2 }}>
                <Button variant="contained" onClick={() => navigate('/app/exercises')}>
                    Gestionar ejercicios
                </Button>
                <Button variant="contained" onClick={() => navigate('/app/admin/events')}>
                    Gestionar eventos
                </Button>
                <Button variant="outlined" onClick={() => void load()}>
                    Actualizar datos
                </Button>
            </Box>

            {error ? (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            ) : null}

            {loading ? (
                <Box sx={{ display: 'grid', placeItems: 'center', minHeight: 200 }}>
                    <CircularProgress />
                </Box>
            ) : (
                <Box
                    sx={{
                        display: 'grid',
                        gap: 2,
                        gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' },
                        alignItems: 'start',
                    }}
                >
                    <Paper sx={{ p: 2 }}>
                        <Typography variant="h6">Entrenadores</Typography>
                        <List>
                            {trainers.map((trainer) => (
                                <ListItem key={trainer.code}>
                                    <ListItemText primary={trainer.name ?? trainer.email ?? 'Sin nombre'} secondary={trainer.email ?? ''} />
                                </ListItem>
                            ))}
                        </List>
                    </Paper>

                    <Paper sx={{ p: 2 }}>
                        <Typography variant="h6">Usuarios (Trainees)</Typography>
                        <List>
                            {trainees.map((user) => (
                                <ListItem key={user.code}>
                                    <ListItemText primary={user.name ?? user.email ?? 'Sin nombre'} secondary={`Rol: ${user.role?.type ?? 'N/A'}`} />
                                    <Box sx={{ minWidth: 180, ml: 2 }}>
                                        <Select
                                            size="small"
                                            fullWidth
                                            value={String(assignments[user.code] ?? '')}
                                            onChange={(event: SelectChangeEvent<string>) => {
                                                const trainerValue = event.target.value;
                                                const trainerId = trainerValue === '' ? null : Number(trainerValue);
                                                void handleAssign(user.code, trainerId);
                                            }}
                                        >
                                            <MenuItem value="">Sin entrenador</MenuItem>
                                            {trainers.map((trainer) => (
                                                <MenuItem key={trainer.code} value={String(trainer.code)}>
                                                    {trainer.name ?? trainer.email ?? trainer.code}
                                                </MenuItem>
                                            ))}
                                        </Select>
                                    </Box>
                                </ListItem>
                            ))}
                        </List>
                    </Paper>
                </Box>
            )}
        </Box>
    );
}
