import axiosClient from '../../../lib/axios/axiosClient';

export interface RoutineResponse {
    id: number;
    name: string;
    trainerCode: number;
    trainerName: string;
    traineeCode: number;
    traineeName: string;
}

export const routineService = {
    getByTrainer: async (trainerId: number): Promise<RoutineResponse[]> => {
        const response = await axiosClient.get<RoutineResponse[]>(`/routines/trainer/${trainerId}`);
        return response.data;
    },
};
