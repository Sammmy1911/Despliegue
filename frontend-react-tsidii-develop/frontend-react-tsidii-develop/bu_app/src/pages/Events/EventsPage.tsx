import React, { useEffect } from 'react';
import {
    Container,
    Grid,
    Typography,
    TextField,
    InputAdornment,
    Box,
    Button,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    List,
    ListItem,
    ListItemText,
    Divider,
    Paper,
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import { useAppDispatch, useAppSelector } from '../../store/hooks';
import { setEvents, setLoading, setError } from '../../store/slices/eventsSlice';
import type { Event } from '../../store/slices/eventsSlice';
import { EventCard } from './components/EventCard';
import { Loading, ErrorDisplay, EmptyState, PageHeader } from '../../components/common';
import { eventsService } from '../../services/events.service';
import { eventPlacesService, type EventPlace } from '../../services/eventPlaces.service';
import { useAuth } from '../../context/useAuth';
import { getRegisteredEventIds, saveRegisteredEventIds } from '../../services/user.service';

const EventsPageComponent: React.FC = () => {
    const dispatch = useAppDispatch();
    const { events, loading, error } = useAppSelector((state) => state.events);
    const { user } = useAuth();
    const currentUserEmail = user?.email;
    const currentUserKey = currentUserEmail ?? 'anonymous';
    const [registrationAdjustments, setRegistrationAdjustments] = React.useState<Record<number, number>>({});
    const [searchTerm, setSearchTerm] = React.useState('');
    const [selectedEvent, setSelectedEvent] = React.useState<Event | null>(null);
    const [eventPlaces, setEventPlaces] = React.useState<EventPlace[]>([]);
    const [detailsDialogOpen, setDetailsDialogOpen] = React.useState(false);
    const [registeredEventIds, setRegisteredEventIds] = React.useState<number[]>(() =>
        getRegisteredEventIds(currentUserKey)
    );

    const fetchEvents = React.useCallback(async (): Promise<void> => {
        dispatch(setLoading(true));
        try {
            const data = await eventsService.getEvents();
            dispatch(setEvents(data));
            dispatch(setError(null));
        } catch {
            dispatch(setError('Failed to fetch events'));
        } finally {
            dispatch(setLoading(false));
        }
    }, [dispatch]);

    const fetchEventPlaces = React.useCallback(async (): Promise<void> => {
        try {
            const data = await eventPlacesService.getEventPlaces();
            setEventPlaces(data);
        } catch {
            // Keep event list visible even if event-place relations fail.
        }
    }, []);

    const adjustRegistrationCount = (eventId: number, adjustment: number): void => {
        setRegistrationAdjustments((prev) => {
            const next = { ...prev };
            const current = next[eventId] ?? 0;
            const updated = current + adjustment;
            if (updated === 0) {
                delete next[eventId];
            } else {
                next[eventId] = updated;
            }
            return next;
        });
    };

    const updateRegisteredEvents = (ids: number[], eventId: number, adjustment: number): void => {
        saveRegisteredEventIds(currentUserKey, ids);
        setRegisteredEventIds(ids);
        adjustRegistrationCount(eventId, adjustment);
    };

    const handleRegister = (eventId: number): void => {
        updateRegisteredEvents(Array.from(new Set([...registeredEventIds, eventId])), eventId, 1);
    };

    const handleUnregister = (eventId: number): void => {
        updateRegisteredEvents(registeredEventIds.filter((id) => id !== eventId), eventId, -1);
    };

    const handleOpenDetails = (event: Event): void => {
        setSelectedEvent(event);
        setDetailsDialogOpen(true);
    };

    useEffect(() => {
        void fetchEvents();
    }, [fetchEvents]);

    useEffect(() => {
        const loadEventPlaces = async (): Promise<void> => {
            await fetchEventPlaces();
        };
        void loadEventPlaces();
    }, [fetchEventPlaces]);

    const filteredEvents = React.useMemo(() => {
        return events.filter(
            (event) =>
                event.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                event.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
                event.location.toLowerCase().includes(searchTerm.toLowerCase())
        );
    }, [searchTerm, events]);

    const getEventPreview = (event: Event): Event => {
        const relation = eventPlaces.find((place) => place.eventId === event.id);
        return {
            ...event,
            location: relation?.placeName || event.location,
            date: relation?.startDate || event.date,
            time: relation?.endDate || event.time,
        };
    };

    const selectedEventPlaces = selectedEvent
        ? eventPlaces.filter((relation) => relation.eventId === selectedEvent.id)
        : [];

    if (loading) {
        return <Loading message="Loading events..." />;
    }

    if (error) {
        return <ErrorDisplay message={error} onRetry={fetchEvents} />;
    }

    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            <PageHeader
                breadcrumbItems={[{ label: 'Eventos', to: '/events' }]}
                title="Eventos disponibles"
                subtitle="Inscríbete en eventos y revisa los lugares donde se realizan."
                showNavigation={false}
            />

            <Paper sx={{ mb: 4, p: 3, bgcolor: 'background.paper', borderRadius: 3, boxShadow: 1 }}>
                <Box
                    sx={{
                        display: 'flex',
                        flexDirection: { xs: 'column', sm: 'row' },
                        gap: 2,
                        alignItems: 'center',
                        justifyContent: 'space-between',
                    }}
                >
                    <TextField
                        placeholder="Buscar eventos..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        fullWidth
                        slotProps={{
                            input: {
                                startAdornment: (
                                    <InputAdornment position="start">
                                        <SearchIcon />
                                    </InputAdornment>
                                ),
                            },
                        }}
                        variant="outlined"
                    />
                </Box>
            </Paper>

            {filteredEvents.length === 0 ? (
                <EmptyState
                    title="No se encontraron eventos"
                    description={
                        searchTerm
                            ? 'Intenta ajustar tus criterios de búsqueda.'
                            : 'Vuelve más tarde para ver próximos eventos.'
                    }
                />
            ) : (
                <Grid container spacing={3}>
                    {filteredEvents.map((event) => {
                        const previewEvent = getEventPreview(event);
                        const isRegistered = registeredEventIds.includes(event.id);
                        const currentRegistrations = previewEvent.registered + (registrationAdjustments[event.id] ?? 0);

                        return (
                            // @ts-expect-error MUI Grid v9 typing mismatch with legacy xs/item props
                            <Grid item xs={12} sm={6} md={4} key={event.id}>
                                <EventCard
                                    event={previewEvent}
                                    onViewDetails={handleOpenDetails}
                                    onRegister={handleRegister}
                                    onUnregister={handleUnregister}
                                    isRegistered={isRegistered}
                                    currentRegistrations={currentRegistrations}
                                />
                            </Grid>
                        );
                    })}
                </Grid>
            )}

            <Dialog open={detailsDialogOpen} onClose={() => setDetailsDialogOpen(false)} maxWidth="sm" fullWidth>
                <DialogTitle>{selectedEvent ? selectedEvent.name : 'Detalles del evento'}</DialogTitle>
                <DialogContent>
                    {selectedEvent ? (
                        <>
                            <Typography variant="subtitle1" sx={{ mb: 1 }}>
                                {selectedEvent.description}
                            </Typography>
                            <Typography variant="body2" color="textSecondary" sx={{ mb: 2 }}>
                                Programado para {selectedEvent.date} a las {selectedEvent.time}
                                {selectedEvent.location ? ` en ${selectedEvent.location}` : ''}
                            </Typography>
                            <Typography variant="body2" color="textSecondary" sx={{ mb: 2 }}>
                                Gestionado por {selectedEvent.managerName ?? `código ${selectedEvent.managerCode ?? 'N/A'}`}
                            </Typography>
                            {selectedEventPlaces.length > 0 ? (
                                <List>
                                    {selectedEventPlaces.map((relation, index) => (
                                        <React.Fragment key={relation.id}>
                                            <ListItem disablePadding>
                                                <ListItemText
                                                    primary={relation.placeName}
                                                    secondary={`Schedule: ${relation.startDate} - ${relation.endDate}`}
                                                />
                                            </ListItem>
                                            {index < selectedEventPlaces.length - 1 && <Divider />}
                                        </React.Fragment>
                                    ))}
                                </List>
                            ) : (
                                <Typography color="textSecondary">
                                    Aún no hay un lugar asignado para este evento.
                                </Typography>
                            )}
                        </>
                    ) : (
                        <Typography color="textSecondary">
                            No event selected.
                        </Typography>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDetailsDialogOpen(false)}>Cerrar</Button>
                </DialogActions>
            </Dialog>
        </Container>
    );
};

export default EventsPageComponent;
