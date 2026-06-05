import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

export interface UserStats {
    weeklyProgress: number[];
    monthlyProgress: number[];
    totalWorkouts: number;
    streak: number;
}

interface UserState {
    stats: UserStats | null;
    loading: boolean;
    error: string | null;
}

const initialState: UserState = {
    stats: null,
    loading: false,
    error: null,
};

const userSlice = createSlice({
    name: 'user',
    initialState,
    reducers: {
        setStats: (state, action: PayloadAction<UserStats>) => {
            state.stats = action.payload;
            state.error = null;
        },
        setLoading: (state, action: PayloadAction<boolean>) => {
            state.loading = action.payload;
        },
        setError: (state, action: PayloadAction<string | null>) => {
            state.error = action.payload;
        },
    },
});

export const { setStats, setLoading, setError } = userSlice.actions;
export default userSlice.reducer;
