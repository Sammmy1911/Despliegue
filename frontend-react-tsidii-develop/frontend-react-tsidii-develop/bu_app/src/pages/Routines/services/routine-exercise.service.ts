import axiosClient from '../../../lib/axios/axiosClient';

export interface RoutineExerciseRequest {
    routineId: number;
    exerciseId: number;
}

export interface RoutineExerciseResponse {
    id: number;
    routineId: number;
    routineName: string;
    exerciseId: number;
    exerciseName: string;
}

export const routineExerciseService = {
    create: async (data: RoutineExerciseRequest): Promise<RoutineExerciseResponse> => {
        const response = await axiosClient.post<RoutineExerciseResponse>('/routine-exercises', data);
        return response.data;
    },
};
