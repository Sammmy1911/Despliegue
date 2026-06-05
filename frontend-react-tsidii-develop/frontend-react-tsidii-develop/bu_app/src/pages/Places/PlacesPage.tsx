import React, { useEffect, useMemo } from 'react';
import {
    Container,
    Grid,
    Box,
    Typography,
    TextField,
    InputAdornment,
    Button,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Paper,
    List,
    ListItem,
    ListItemText,
    Divider,
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';

import { useAppDispatch, useAppSelector } from '../../store/hooks';
import { setPlaces, setLoading, setError, type Place } from '../../store/slices/placeSlice';
import { PlaceCard } from './components/PlaceCard';
import { Loading, ErrorDisplay, EmptyState, PageHeader } from '../../components/common';
import { placesService } from '../../services/places.service';
import { eventPlacesService, type EventPlace } from '../../services/eventPlaces.service';
import { bookingService } from '../../services/booking.service';
import { useAuth } from '../../context/useAuth';

const PlacesPageComponent: React.FC = () => {
    const dispatch = useAppDispatch();
    const { places, loading, error } = useAppSelector((state) => state.places);
    const { user } = useAuth();
    const currentUserEmail = user?.email;
    const currentUserKey = currentUserEmail ?? 'anonymous';
    const [searchTerm, setSearchTerm] = React.useState('');
    const [detailsDialogOpen, setDetailsDialogOpen] = React.useState(false);
    const [selectedPlace, setSelectedPlace] = React.useState<Place | null>(null);
    const [eventPlaces, setEventPlaces] = React.useState<EventPlace[]>([]);
    const [bookedPlaceIds, setBookedPlaceIds] = React.useState<number[]>(() =>
        bookingService.getBookedPlaceIds(currentUserKey)
    );

    const fetchPlaces = React.useCallback(async (): Promise<void> => {
        dispatch(setLoading(true));
        try {
            const data = await placesService.getPlaces();
            dispatch(setPlaces(data));
            dispatch(setError(null));
        } catch {
            dispatch(setError('Failed to fetch places'));
        } finally {
            dispatch(setLoading(false));
        }
    }, [dispatch]);

    useEffect(() => {
        void fetchPlaces();
    }, [fetchPlaces]);

    useEffect(() => {
        const loadEventPlaces = async (): Promise<void> => {
            try {
                const data = await eventPlacesService.getEventPlaces();
                setEventPlaces(data);
            } catch {
                // ignore event place fetch failures
            }
        };

        void loadEventPlaces();
    }, []);

    const placeEventsMap = useMemo(() => {
        return eventPlaces.reduce<Record<number, EventPlace[]>>((acc, relation) => {
            const items = acc[relation.placeId] ?? [];
            items.push(relation);
            acc[relation.placeId] = items;
            return acc;
        }, {});
    }, [eventPlaces]);

    const handleBook = (spaceId: number): void => {
        const alreadyBooked = bookedPlaceIds.includes(spaceId);
        const updated = alreadyBooked
            ? bookedPlaceIds.filter((id) => id !== spaceId)
            : [...bookedPlaceIds, spaceId];

        bookingService.saveBookedPlaceIds(currentUserKey, updated);
        setBookedPlaceIds(updated);
    };

    const handleViewDetails = async (spaceId: number): Promise<void> => {
        try {
            const place = await placesService.getPlaceById(spaceId);
            setSelectedPlace(place);
            setDetailsDialogOpen(true);
        } catch {
            // ignore
        }
    };

    const filteredPlaces = React.useMemo(() => {
        return places.filter(
            (space) =>
                space.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                space.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
                space.type.toLowerCase().includes(searchTerm.toLowerCase())
        );
    }, [searchTerm, places]);

    if (loading) {
        return <Loading message="Loading places..." />;
    }

    if (error) {
        return <ErrorDisplay message={error} onRetry={fetchPlaces} />;
    }

    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            <PageHeader
                breadcrumbItems={[{ label: 'Places', to: '/places' }]}
                title="Available Places"
                subtitle="Browse and book available places for your events"
            />

            <Paper sx={{ mb: 4, p: 3, bgcolor: 'background.paper', borderRadius: 3, boxShadow: 1 }}>
                <Box
                    sx={{
                        display: 'flex',
                        flexDirection: { xs: 'column', sm: 'row' },
                        alignItems: 'center',
                        justifyContent: 'space-between',
                        gap: 2,
                    }}
                >
                    <TextField
                        placeholder="Search places..."
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
                    <Box sx={{ minWidth: 180 }}>
                        <Typography variant="subtitle2" color="textSecondary">
                            Booking and place details are loaded from the database.
                        </Typography>
                    </Box>
                </Box>
            </Paper>

            {filteredPlaces.length === 0 ? (
                <EmptyState
                    title="No places found"
                    description={
                        searchTerm
                            ? 'Try adjusting your search criteria'
                            : 'No places available at the moment'
                    }
                />
            ) : (
                <Grid container spacing={3}>
                    {filteredPlaces.map((space) => {
                        const scheduled = placeEventsMap[space.id] ?? [];
                        const nextSchedule = scheduled[0]?.startDate ?? 'No scheduled events';
                        const isBooked = bookedPlaceIds.includes(space.id);
                        const previewSpace = {
                            ...space,
                            schedule: nextSchedule,
                        };

                        return (
                            // @ts-expect-error MUI Grid v9 typing mismatch with legacy xs/item props
                            <Grid item xs={12} sm={6} md={4} key={space.id}>
                                <PlaceCard
                                    space={previewSpace}
                                    onBook={handleBook}
                                    onViewDetails={handleViewDetails}
                                    isBooked={isBooked}
                                />
                            </Grid>
                        );
                    })}
                </Grid>
            )}

            <Dialog open={detailsDialogOpen} onClose={() => setDetailsDialogOpen(false)} maxWidth="sm" fullWidth>
                <DialogTitle>{selectedPlace?.name ?? 'Place details'}</DialogTitle>
                <DialogContent>
                    {selectedPlace ? (
                        <>
                            <Typography variant="body1" sx={{ mb: 1 }}>
                                <strong>Status:</strong> {selectedPlace.description}
                            </Typography>
                            <Typography variant="body2" color="textSecondary" sx={{ mb: 2 }}>
                                {selectedPlace.type ? `Type: ${selectedPlace.type}` : ''}
                            </Typography>
                            <Typography variant="subtitle2" sx={{ mb: 1 }}>
                                Scheduled events for this place
                            </Typography>
                            {placeEventsMap[selectedPlace.id]?.length ? (
                                <List>
                                    {placeEventsMap[selectedPlace.id].map((schedule, index) => (
                                        <React.Fragment key={schedule.id}>
                                            <ListItem disablePadding>
                                                <ListItemText
                                                    primary={schedule.eventName}
                                                    secondary={`${schedule.startDate} - ${schedule.endDate}`}
                                                />
                                            </ListItem>
                                            {index < placeEventsMap[selectedPlace.id].length - 1 && <Divider />}
                                        </React.Fragment>
                                    ))}
                                </List>
                            ) : (
                                <Typography color="textSecondary">
                                    No scheduled events have been assigned to this place yet.
                                </Typography>
                            )}
                        </>
                    ) : (
                        <Typography color="textSecondary">No place selected.</Typography>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDetailsDialogOpen(false)}>Close</Button>
                    {selectedPlace && (
                        <Button
                            variant={bookedPlaceIds.includes(selectedPlace.id) ? 'outlined' : 'contained'}
                            color="primary"
                            onClick={() => {
                                handleBook(selectedPlace.id);
                                setDetailsDialogOpen(false);
                            }}
                        >
                            {bookedPlaceIds.includes(selectedPlace.id) ? 'Cancel booking' : 'Book place'}
                        </Button>
                    )}
                </DialogActions>
            </Dialog>

        </Container>
    );
};

export default PlacesPageComponent;
