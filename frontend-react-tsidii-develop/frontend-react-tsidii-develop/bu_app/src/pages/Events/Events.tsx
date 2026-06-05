import React, { useEffect } from 'react';
import {
    Box,
    Container,
    Grid,
    InputAdornment,
    Stack,
    TextField,
    Typography,
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';

import { useAppDispatch, useAppSelector } from '../../store/hooks';
import { setError, setLoading, setEvents } from '../../store/slices/eventsSlice';
import { Loading } from '../../components/common/Loading';
import { ErrorDisplay } from '../../components/common/ErrorDisplay';
import { EmptyState } from '../../components/common/EmptyState';
import { EventCard } from './components/EventCard';

import { eventsPageService } from './services/eventsPage.service';

export const Events: React.FC = () => {
    const dispatch = useAppDispatch();
    const { events, loading, error } = useAppSelector((state) => state.events);

    const [searchTerm, setSearchTerm] = React.useState('');

    const fetchEvents = React.useCallback(async (): Promise<void> => {
        dispatch(setLoading(true));
        try {
            const data = await eventsPageService.getEvents();
            dispatch(setEvents(data));
            dispatch(setError(null));
        } catch {
            dispatch(setError('Failed to fetch events'));
        } finally {
            dispatch(setLoading(false));
        }
    }, [dispatch]);

    useEffect(() => {
        void fetchEvents();
    }, [fetchEvents]);

    const filteredEvents = React.useMemo(() => {
        return events.filter(
            (event) =>
                event.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                event.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
                event.location.toLowerCase().includes(searchTerm.toLowerCase())
        );
    }, [searchTerm, events]);


    if (loading) {
        return <Loading message="Loading events..." />;
    }

    if (error) {
        return <ErrorDisplay message={error} onRetry={fetchEvents} />;
    }

    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            <Box sx={{ mb: 4 }}>
                <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 'bold' }}>
                    Available Events
                </Typography>
                <Typography variant="body1" color="textSecondary">
                    Register for exciting fitness and wellness events
                </Typography>
            </Box>

            <Stack direction="row" spacing={2} sx={{ mb: 4, display: 'flex', alignItems: 'center' }}>
                <TextField
                    placeholder="Search events..."
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
            </Stack>

            {filteredEvents.length === 0 ? (
                <EmptyState
                    title="No events found"
                    description={
                        searchTerm
                            ? 'Try adjusting your search criteria'
                            : 'Check back later for upcoming events'
                    }
                />
            ) : (
                <Grid container spacing={3}>
                    {filteredEvents.map((event) => (
                        // @ts-expect-error MUI Grid v9 typing mismatch with legacy xs/item props
                        <Grid item xs={12} sm={6} md={4} key={event.id}>
                            <EventCard event={event} />
                        </Grid>
                    ))}
                </Grid>
            )}

        </Container>
    );
};

export default Events;

