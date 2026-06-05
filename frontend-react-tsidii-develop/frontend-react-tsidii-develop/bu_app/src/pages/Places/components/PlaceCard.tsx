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
import LocationOnIcon from '@mui/icons-material/LocationOn';
import EventNoteIcon from '@mui/icons-material/EventNote';

import type { Place } from '../../../store/slices/placeSlice';


interface PlaceCardProps {
    space: Place;
    onBook?: (spaceId: number) => void;
    onViewDetails?: (spaceId: number) => void;
    isBooked?: boolean;
}

export const PlaceCard: React.FC<PlaceCardProps> = ({ space, onBook, onViewDetails, isBooked }) => {
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
            {space.image && (
                <CardMedia component="img" height="200" image={space.image} alt={space.name} />
            )}
            <Box
                sx={{
                    p: 2,
                    borderBottom: 1,
                    borderColor: 'divider',
                    bgcolor: 'background.default',
                    display: 'flex',
                    flexWrap: 'wrap',
                    alignItems: 'center',
                    gap: 1,
                }}
            >
                <Chip label={space.type} size="small" color="primary" variant="outlined" />
                <Chip
                    label={`${space.capacity} capacity`}
                    size="small"
                    variant="outlined"
                />
            </Box>
            <CardContent sx={{ flexGrow: 1, p: 3 }}>
                <Typography variant="h6" component="h2" gutterBottom sx={{ fontWeight: 700 }}>
                    {space.name}
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                    {space.description}
                </Typography>

                <Box sx={{ display: 'grid', gap: 1 }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <LocationOnIcon fontSize="small" color="action" />
                        <Typography variant="caption">Capacity: {space.capacity}</Typography>
                    </Box>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <EventNoteIcon fontSize="small" color="action" />
                        <Typography variant="caption">{space.schedule}</Typography>
                    </Box>
                    {space.amenities && space.amenities.length > 0 && (
                        <Box
                            sx={{
                                display: 'flex',
                                flexWrap: 'wrap',
                                gap: 1,
                                mt: 1,
                            }}
                        >
                            {space.amenities.map((amenity: string) => (
                                <Chip
                                    key={amenity}
                                    label={amenity}
                                    size="small"
                                    variant="filled"
                                />
                            ))}
                        </Box>
                    )}
                </Box>
            </CardContent>

            <CardActions sx={{ p: 2, gap: 1 }}>
                <Button
                    size="small"
                    color="primary"
                    variant="outlined"
                    onClick={() => onViewDetails?.(space.id)}
                    sx={{ flex: 1 }}
                >
                    Details
                </Button>
                <Button
                    size="small"
                    color="primary"
                    variant={isBooked ? 'outlined' : 'contained'}
                    onClick={() => onBook?.(space.id)}
                    sx={{ flex: 1 }}
                >
                    {isBooked ? 'Booked' : 'Book'}
                </Button>
            </CardActions>
        </Card>
    );
};
