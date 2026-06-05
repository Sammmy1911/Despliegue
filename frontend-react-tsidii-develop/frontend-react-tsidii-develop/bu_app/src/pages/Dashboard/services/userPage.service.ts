import type { UserStats } from '../../../store/slices/userSlice';

const defaultStats: UserStats = {
    weeklyProgress: [65, 75, 85, 70, 90, 80, 95],
    monthlyProgress: [72, 78, 84, 90, 86],
    totalWorkouts: 28,
    streak: 5,
};

export const userPageService = {
    async getStats(): Promise<UserStats> {
        return Promise.resolve(defaultStats);
    },

    async generateProgressReport(): Promise<Blob> {
        return new Blob([], { type: 'application/pdf' });
    },
};

