import axiosClient from '../../../lib/axios/axiosClient';

export interface ProgressResponse {
    id: number;
    repetitions: number;
    time: string;
    performedAt: string;
    stressLevelId: number;
    stressLevelName: string;
    progressTypeId: number;
    progressTypeName: string;
    traineeId: number;
    traineeName: string;
    routineId: number;
    routineName: string;
}

export const progressService = {
    getByTrainee: async (traineeId: number): Promise<ProgressResponse[]> => {
        const from = '2000-01-01T00:00:00';
        const to = '2100-01-01T00:00:00';
        const response = await axiosClient.get<ProgressResponse[]>('/progresses/search', {
            params: { from, to, traineeId },
        });
        return response.data;
    },
};
