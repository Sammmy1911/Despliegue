import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

export interface Event {
    id: number;
    name: string;
    description: string;
    date: string;
    time: string;
    location: string;
    capacity: number;
    registered: number;
    image?: string;
    managerName?: string;
    managerCode?: number;
}

interface EventsState {
    events: Event[];
    loading: boolean;
    error: string | null;
}

const initialState: EventsState = {
    events: [],
    loading: false,
    error: null,
};

const eventsSlice = createSlice({
    name: 'events',
    initialState,
    reducers: {
        setEvents: (state, action: PayloadAction<Event[]>) => {
            state.events = action.payload;
            state.error = null;
        },
        setLoading: (state, action: PayloadAction<boolean>) => {
            state.loading = action.payload;
        },
        setError: (state, action: PayloadAction<string | null>) => {
            state.error = action.payload;
        },
        addEvent: (state, action: PayloadAction<Event>) => {
            state.events.push(action.payload);
        },
        updateEvent: (state, action: PayloadAction<Event>) => {
            const index = state.events.findIndex((e) => e.id === action.payload.id);
            if (index !== -1) {
                state.events[index] = action.payload;
            }
        },
        deleteEvent: (state, action: PayloadAction<number>) => {
            state.events = state.events.filter((e) => e.id !== action.payload);
        },
    },
});

export const { setEvents, setLoading, setError, addEvent, updateEvent, deleteEvent } = eventsSlice.actions;
export default eventsSlice.reducer;
