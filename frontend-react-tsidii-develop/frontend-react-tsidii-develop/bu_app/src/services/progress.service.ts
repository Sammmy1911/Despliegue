import axiosClient from '../lib/axios/axiosClient';
import type {
    AggregatedProgressResponse,
    ProgressRequest,
    ProgressResponse,
    SelectOption,
} from '../types/api';

type ProgressTypeApiResponse = {
    id: number;
    type: string;
};

type StressLevelApiResponse = {
    id: number;
    level: string;
};

export async function createProgress(payload: ProgressRequest): Promise<ProgressResponse> {
    const response = await axiosClient.post<ProgressResponse>('/progresses', payload);

    return response.data;
}

export async function updateProgress(id: number, payload: ProgressRequest): Promise<ProgressResponse> {
    const response = await axiosClient.put<ProgressResponse>(`/progresses/${id}`, payload);

    return response.data;
}

export async function searchProgresses(from: string, to: string, traineeId?: number): Promise<ProgressResponse[]> {
    const response = await axiosClient.get<ProgressResponse[]>('/progresses/search', {
        params: { from, to, traineeId },
    });

    return response.data;
}

export async function getAggregatedProgress(
    period: 'daily' | 'weekly',
    from: string,
    to: string,
    traineeId?: number,
): Promise<AggregatedProgressResponse[]> {
    const response = await axiosClient.get<AggregatedProgressResponse[]>('/progresses/aggregated', {
        params: { period, from, to, traineeId },
    });

    return response.data;
}

export async function getProgressTypes(): Promise<SelectOption[]> {
    const response = await axiosClient.get<ProgressTypeApiResponse[]>('/progress-types/all');

    return response.data.map((item) => ({
        id: item.id,
        name: item.type,
    }));
}

export async function getStressLevels(): Promise<SelectOption[]> {
    const response = await axiosClient.get<StressLevelApiResponse[]>('/stress-levels/all');

    return response.data.map((item) => ({
        id: item.id,
        name: item.level,
    }));
}
