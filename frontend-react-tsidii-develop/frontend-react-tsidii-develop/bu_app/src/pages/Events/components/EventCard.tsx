import React from 'react';
import {
    Card,
    CardContent,
    CardMedia,
    CardActions,
    Button,
    Typography,
    Chip,
    Box,
} from '@mui/material';
import EventNoteIcon from '@mui/icons-material/EventNote';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import GroupIcon from '@mui/icons-material/Group';
import type { Event } from '../../../store/slices/eventsSlice';

interface EventCardProps {
    event: Event;
    onRegister?: (eventId: number) => void;
    onUnregister?: (eventId: number) => void;
    onViewDetails?: (event: Event) => void;
    isRegistered?: boolean;
    currentRegistrations?: number;
}

export const EventCard: React.FC<EventCardProps> = ({
    event,
    onRegister,
    onUnregister,
    onViewDetails,
    isRegistered = false,
    currentRegistrations,
}) => {
    const registeredCount = currentRegistrations ?? event.registered;
    const spotsAvailable = event.capacity - registeredCount;
    const isFull = spotsAvailable <= 0;

    return (
        <Card
            sx={{
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                border: 1,
                borderColor: 'divider',
                bgcolor: 'background.paper',
                overflow: 'hidden',
                transition: 'transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out',
                '&:hover': {
                    transform: 'translateY(-6px)',
                    boxShadow: 3,
                },
            }}
        >
            {event.image && (
                <CardMedia component="img" height="200" image={event.image} alt={event.name} />
            )}
            <Box sx={{ p: 2, borderBottom: 1, borderColor: 'divider', bgcolor: 'background.default' }}>
                <Box
                    sx={{
                        display: 'flex',
                        flexWrap: 'wrap',
                        alignItems: 'center',
                        gap: 1,
                    }}
                >
                    <Chip label={event.date} size="small" variant="outlined" />
                    <Chip
                        label={isFull ? 'Lleno' : `${spotsAvailable} lugares`}
                        size="small"
                        color={isFull ? 'error' : 'success'}
                        variant="outlined"
                    />
                </Box>
            </Box>
            <CardContent sx={{ flexGrow: 1, p: 3 }}>
                <Typography variant="h6" component="h2" gutterBottom sx={{ fontWeight: 700 }}>
                    {event.name}
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                    {event.description}
                </Typography>

                <Box sx={{ display: 'grid', gap: 1 }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <EventNoteIcon fontSize="small" color="action" />
                        <Typography variant="caption">{event.time}</Typography>
                    </Box>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <LocationOnIcon fontSize="small" color="action" />
                        <Typography variant="caption">{event.location}</Typography>
                    </Box>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <GroupIcon fontSize="small" color="action" />
                        <Typography variant="caption">
                            {registeredCount}/{event.capacity}
                        </Typography>
                    </Box>
                </Box>
            </CardContent>

            <CardActions sx={{ p: 2, gap: 1, flexWrap: 'wrap' }}>
                {onViewDetails && (
                    <Button size="small" fullWidth onClick={() => onViewDetails(event)}>
                        Ver detalles
                    </Button>
                )}
                {onRegister || onUnregister ? (
                    isRegistered ? (
                        <Button
                            size="small"
                            color="error"
                            fullWidth
                            onClick={() => onUnregister?.(event.id)}
                            variant="outlined"
                        >
                            Desinscribirse
                        </Button>
                    ) : (
                        <Button
                            size="small"
                            color="primary"
                            fullWidth
                            disabled={isFull}
                            onClick={() => onRegister?.(event.id)}
                            variant="contained"
                        >
                            Inscribirse
                        </Button>
                    )
                ) : null}
            </CardActions>
        </Card>
    );
};
