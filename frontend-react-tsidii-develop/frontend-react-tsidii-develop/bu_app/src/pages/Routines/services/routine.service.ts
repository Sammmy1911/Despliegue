import axiosClient from '../../../lib/axios/axiosClient';

export interface RoutineResponse {
    id: number;
    name: string;
    trainerCode: number;
    trainerName: string;
    traineeCode: number;
    traineeName: string;
}

export interface RoutineRequest {
    name: string;
    trainerCode: number;
    traineeCode?: number;
}

export const routineService = {
    create: async (data: RoutineRequest): Promise<RoutineResponse> => {
        const response = await axiosClient.post<RoutineResponse>('/routines', data);
        return response.data;
    },
};
