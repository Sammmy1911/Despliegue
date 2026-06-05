import axiosClient from '../../../lib/axios/axiosClient';

export interface ExerciseResponse {
    id: number;
    name: string;
    description: string;
    length: number;
    video?: string;
    difficultyName: string;
    typeName: string;
}

export const exerciseService = {
    getAll: async (): Promise<ExerciseResponse[]> => {
        const response = await axiosClient.get<ExerciseResponse[]>('/exercises');
        return response.data;
    },
};
