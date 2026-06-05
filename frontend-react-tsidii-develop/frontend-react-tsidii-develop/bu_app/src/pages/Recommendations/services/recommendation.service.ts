import axiosClient from '../../../lib/axios/axiosClient';

export interface RecommendationRequest {
    description: string;
    trainerId: number;
    progressId: number;
}

export interface RecommendationResponse {
    id: number;
    description: string;
    trainerName: string;
    progressId: number;
}

export const recommendationService = {
    create: async (data: RecommendationRequest): Promise<RecommendationResponse> => {
        const response = await axiosClient.post<RecommendationResponse>('/recommendations', data);
        return response.data;
    },
};
