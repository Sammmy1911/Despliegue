import axiosClient from '../lib/axios/axiosClient';
import type { ExerciseRequest, ExerciseResponse, SelectOption } from '../types/api';

export async function getExercises(custom?: boolean): Promise<ExerciseResponse[]> {
    const response = await axiosClient.get<ExerciseResponse[]>('/exercises', {
        params: custom === undefined ? undefined : { custom },
    });

    return response.data;
}

export async function createExercise(payload: ExerciseRequest): Promise<ExerciseResponse> {
    const response = await axiosClient.post<ExerciseResponse>('/exercises', payload);

    return response.data;
}

export async function updateExercise(id: number, payload: ExerciseRequest): Promise<ExerciseResponse> {
    const response = await axiosClient.put<ExerciseResponse>(`/exercises/${id}`, payload);

    return response.data;
}

export async function deleteExercise(id: number): Promise<void> {
    await axiosClient.delete(`/exercises/${id}`);
}

export async function getExerciseTypes(): Promise<SelectOption[]> {
    try {
        const response = await axiosClient.get<SelectOption[]>('/exercise-types');

        return response.data;
    } catch {
        const response = await axiosClient.get<SelectOption[]>('/exercise-types/all');

        return response.data;
    }
}

export async function getExerciseDifficulties(): Promise<SelectOption[]> {
    try {
        const response = await axiosClient.get<SelectOption[]>('/exercise-difficulties');

        return response.data;
    } catch {
        const response = await axiosClient.get<SelectOption[]>('/exercise-difficulties/all');

        return response.data;
    }
}
