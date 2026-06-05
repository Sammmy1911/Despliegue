import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

export interface Place {
    id: number;
    name: string;
    description: string;
    type: string;
    capacity: number;
    schedule: string;
    amenities: string[];
    image?: string;
}

interface PlacesState {
    places: Place[];
    loading: boolean;
    error: string | null;
}

const initialState: PlacesState = {
    places: [],
    loading: false,
    error: null,
};

const placesSlice = createSlice({
    name: 'places',
    initialState,
    reducers: {
        setPlaces: (state, action: PayloadAction<Place[]>) => {
            state.places = action.payload;
            state.error = null;
        },
        setLoading: (state, action: PayloadAction<boolean>) => {
            state.loading = action.payload;
        },
        setError: (state, action: PayloadAction<string | null>) => {
            state.error = action.payload;
        },
        addPlace: (state, action: PayloadAction<Place>) => {
            state.places.push(action.payload);
        },
        updatePlace: (state, action: PayloadAction<Place>) => {
            const index = state.places.findIndex((p) => p.id === action.payload.id);
            if (index !== -1) {
                state.places[index] = action.payload;
            }
        },
        deletePlace: (state, action: PayloadAction<number>) => {
            state.places = state.places.filter((p) => p.id !== action.payload);
        },
    },
});

export const { setPlaces, setLoading, setError, addPlace, updatePlace, deletePlace } = placesSlice.actions;
export default placesSlice.reducer;
