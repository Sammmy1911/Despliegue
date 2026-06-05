import React, { useEffect, useRef } from 'react';
import type { Chart as ChartJS } from 'chart.js';
import {
    Container,
    Box,
    Stack,
    Button,
    CircularProgress,
    Typography,
    Card,
    CardContent,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import { setStats, setLoading, setError } from '../store/slices/userSlice';
import { ProgressChart } from './Dashboard/components/charts/ProgressChart';
import { StatsOverview } from './Dashboard/components/charts/StatsOverview';
import { Loading, ErrorDisplay, PageHeader } from '../components/common';
import { userService } from '../services/user.service';
import FileDownloadIcon from '@mui/icons-material/FileDownload';
import { useAuth } from '../context/useAuth';

const dashboardCards = [
    {
        title: 'Rutinas activas',
        description:
            'Crea y edita planes de entrenamiento con ejercicios globales y personalizados.',
        path: '/app/routines'
    },
    {
        title: 'Seguimiento',
        description:
            'Registra repeticiones, tiempo e intensidad para medir tu avance diario o semanal.',
        path: '/app/progress'
    },
    {
        title: 'Acompañamiento',
        description:
            'Entrenadores pueden revisar rutinas y progreso de los usuarios asignados.',
        path: '/app/trainer'
    },
    {
        title: 'Historial',
        description:
            'Revisa tu historial completo de entrenamientos y progreso registrado.',
        path: '/app/history'
    },
];

const DashboardPage: React.FC = () => {
    const dispatch = useAppDispatch();
    const { stats, loading, error } = useAppSelector((state) => state.user);
    const { user } = useAuth();
    const navigate = useNavigate();
    const [reportLoading, setReportLoading] = React.useState(false);
    const weeklyChartRef = useRef<ChartJS<'line', number[], string> | null>(null);
    const monthlyChartRef = useRef<ChartJS<'line', number[], string> | null>(null);

    const currentUserEmail = user?.email;
    const currentUserName = user?.name ?? currentUserEmail ?? 'User';
    const currentUserKey = currentUserEmail ?? currentUserName;

    const fetchStats = React.useCallback(async (): Promise<void> => {
        dispatch(setLoading(true));
        try {
            const data = await userService.getStats(currentUserKey);
            dispatch(setStats(data));
            dispatch(setError(null));
        } catch {
            dispatch(setError('Failed to fetch statistics'));
        } finally {
            dispatch(setLoading(false));
        }
    }, [currentUserKey, dispatch]);

    useEffect(() => {
        void fetchStats();
    }, [fetchStats]);

    const handleDownloadReport = async (): Promise<void> => {
        setReportLoading(true);
        try {
            if (!stats) {
                return;
            }

            const chartImages = [] as { title: string; dataUrl: string }[];
            const weeklyImage = weeklyChartRef.current?.toBase64Image();
            const monthlyImage = monthlyChartRef.current?.toBase64Image();

            if (weeklyImage) {
                chartImages.push({ title: 'Weekly Progress', dataUrl: weeklyImage });
            }
            if (monthlyImage) {
                chartImages.push({ title: 'Monthly Progress', dataUrl: monthlyImage });
            }

            const blob = await userService.generateProgressReport(currentUserName, stats, chartImages);
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = `progress-report-${new Date().toISOString().split('T')[0]}.pdf`;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
        } finally {
            setReportLoading(false);
        }
    };

    if (loading) {
        return <Loading message="Loading dashboard..." />;
    }

    if (error) {
        return <ErrorDisplay message={error} onRetry={fetchStats} />;
    }

    if (!stats) {
        return (
            <Container maxWidth="lg" sx={{ py: 4 }}>
                <Typography variant="body1" color="textSecondary">
                    No statistics available
                </Typography>
            </Container>
        );
    }

    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            <PageHeader
                breadcrumbItems={[{ label: 'Panel' }]}
                title="Dashboard"
                subtitle={`Bienvenido de nuevo, ${currentUserName}. Aquí tienes tu resumen de progreso.`}
                showNavigation={false}
                showBreadcrumb={false}
            />
            <Box
                sx={{
                    mb: 4,
                    display: 'flex',
                    flexWrap: 'wrap',
                    gap: 2,
                    justifyContent: 'space-between',
                }}
            >
                {dashboardCards.map((card) => (
                    <Card
                        key={card.title}
                        sx={{
                            flex: '1 1 30%',
                            minWidth: 260,
                            maxWidth: 360,
                            height: '100%',
                            cursor: 'pointer',
                            transition: 'transform 0.2s, box-shadow 0.2s',
                            '&:hover': {
                                transform: 'translateY(-4px)',
                                boxShadow: 6
                            }
                        }}
                        onClick={() => navigate(card.path)}
                    >
                        <CardContent>
                            <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>
                                {card.title}
                            </Typography>
                            <Typography color="text.secondary">
                                {card.description}
                            </Typography>
                        </CardContent>
                    </Card>
                ))}
            </Box>

            <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', flexWrap: 'wrap', gap: 2 }}>
                <Typography variant="h5" sx={{ fontWeight: 700, flexGrow: 1 }}>
                    Estadísticas de progreso
                </Typography>
                <Stack direction="row" spacing={2}>
                    <Button
                        variant="contained"
                        color="primary"
                        startIcon={reportLoading ? <CircularProgress size={20} /> : <FileDownloadIcon />}
                        onClick={handleDownloadReport}
                        disabled={reportLoading}
                    >
                        Descargar reporte
                    </Button>
                </Stack>
            </Box>

            <Box sx={{ mb: 4 }}>
                <StatsOverview
                    totalWorkouts={stats.totalWorkouts}
                    currentStreak={stats.streak}
                    weeklyTarget={7}
                    weeklyProgress={
                        stats.weeklyProgress.reduce((a, b) => a + b, 0) /
                        stats.weeklyProgress.length
                    }
                />
            </Box>

            <Box
                sx={{
                    display: 'grid',
                    gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' },
                    gap: 3,
                }}
            >
                <Box>
                    <ProgressChart
                        ref={weeklyChartRef}
                        data={stats.weeklyProgress}
                        labels={['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']}
                        title="Weekly Progress"
                    />
                </Box>
                <Box>
                    <ProgressChart
                        ref={monthlyChartRef}
                        data={stats.monthlyProgress}
                        labels={[
                            'Week 1',
                            'Week 2',
                            'Week 3',
                            'Week 4',
                            'Week 5',
                        ]}
                        title="Monthly Progress"
                    />
                </Box>
            </Box>

        </Container>
    );
};

export default DashboardPage;