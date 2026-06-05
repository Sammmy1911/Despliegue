import axiosClient from '../lib/axios/axiosClient';
import type {
    RoutineExerciseRequest,
    RoutineExerciseResponse,
    RoutineRequest,
    RoutineResponse,
} from '../types/api';

export async function getAllRoutines(): Promise<RoutineResponse[]> {
    const response = await axiosClient.get<RoutineResponse[]>('/routines');

    return response.data;
}

export async function getTrainerRoutines(trainerCode: number): Promise<RoutineResponse[]> {
    const response = await axiosClient.get<RoutineResponse[]>(`/routines/trainer/${trainerCode}`);

    return response.data;
}

export async function getTraineeRoutines(traineeCode: number): Promise<RoutineResponse[]> {
    const response = await axiosClient.get<RoutineResponse[]>(`/routines/trainee/${traineeCode}`);

    return response.data;
}

export async function createRoutine(payload: RoutineRequest): Promise<RoutineResponse> {
    const response = await axiosClient.post<RoutineResponse>('/routines', payload);

    return response.data;
}

export async function updateRoutine(id: number, payload: RoutineRequest): Promise<RoutineResponse> {
    const response = await axiosClient.put<RoutineResponse>(`/routines/${id}`, payload);

    return response.data;
}

export async function deleteRoutine(id: number): Promise<void> {
    await axiosClient.delete(`/routines/${id}`);
}

export async function createRoutineExercise(payload: RoutineExerciseRequest): Promise<RoutineExerciseResponse> {
    const response = await axiosClient.post<RoutineExerciseResponse>('/routine-exercises', payload);

    return response.data;
}
