import axiosClient from '../lib/axiosClient';

export interface Place {
    id: number;
    name: string;
    description: string;
    type: string;
    capacity: number;
    schedule: string;
    amenities: string[];
    image?: string;
}

interface BackendPlaceResponse {
    id: number;
    name: string;
    description: string;
    type: string;
    capacity: number;
    schedule?: string;
    amenities?: string[];
    image?: string;
}

const mapPlaceResponse = (response: BackendPlaceResponse): Place => ({
    id: response.id,
    name: response.name,
    description: response.description,
    type: response.type,
    capacity: response.capacity,
    schedule: response.schedule ?? 'TBD',
    amenities: response.amenities ?? [],
    image: response.image,
});

export const placesService = {
    async getPlaces(): Promise<Place[]> {
        const response = await axiosClient.get<BackendPlaceResponse[]>('/places/all');
        return response.data.map(mapPlaceResponse);
    },

    async getPlaceById(placeId: number): Promise<Place> {
        const response = await axiosClient.get<Place>(`/places/${placeId}`);
        return mapPlaceResponse(response.data);
    },
};
