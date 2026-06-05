import axiosClient from '../lib/axiosClient';

export interface EventPlace {
    id: number;
    eventId: number;
    eventName: string;
    placeId: number;
    placeName: string;
    startDate: string;
    endDate: string;
}

interface BackendEventPlaceResponse {
    id: number;
    startDate?: string;
    endDate?: string;
    eventId: number;
    eventName: string;
    placeId: number;
    placeName: string;
}

const mapEventPlaceResponse = (response: BackendEventPlaceResponse): EventPlace => ({
    id: response.id,
    eventId: response.eventId,
    eventName: response.eventName,
    placeId: response.placeId,
    placeName: response.placeName,
    startDate: response.startDate ?? 'TBD',
    endDate: response.endDate ?? 'TBD',
});

export const eventPlacesService = {
    async getEventPlaces(): Promise<EventPlace[]> {
        const response = await axiosClient.get<BackendEventPlaceResponse[]>('/events-places/all');
        return response.data.map(mapEventPlaceResponse);
    },

    async getEventPlacesByEvent(eventId: number): Promise<EventPlace[]> {
        const allRelations = await this.getEventPlaces();
        return allRelations.filter((relation) => relation.eventId === eventId);
    },
};
