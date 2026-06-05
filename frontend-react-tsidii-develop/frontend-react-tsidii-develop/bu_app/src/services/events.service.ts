import axiosClient from '../lib/axios/axiosClient';
import type { EventRequest, EventResponse } from '../types/api';
import type { Event } from '../store/slices/eventsSlice';

const mapEventResponseToEvent = (response: EventResponse): Event => ({
    id: response.id,
    name: response.name,
    description:
        response.description ||
        `Managed by ${response.managerName ?? `code ${response.managerCode ?? 'N/A'}`}`,
    date: 'TBD',
    time: 'TBD',
    location: 'TBD',
    capacity: 20,
    registered: 0,
    managerCode: response.managerCode ?? undefined,
    managerName: response.managerName ?? undefined,
});

export async function getEvents(): Promise<EventResponse[]> {
    const response = await axiosClient.get<EventResponse[]>('/events/all');
    return response.data;
}

export async function getEventById(id: number): Promise<EventResponse> {
    const response = await axiosClient.get<EventResponse>(`/events/${id}`);
    return response.data;
}

export async function createEvent(payload: EventRequest): Promise<EventResponse> {
    const response = await axiosClient.post<EventResponse>('/events', payload);
    return response.data;
}

export async function updateEvent(id: number, payload: EventRequest): Promise<EventResponse> {
    const response = await axiosClient.put<EventResponse>(`/events/${id}`, payload);
    return response.data;
}

export async function deleteEvent(id: number): Promise<void> {
    await axiosClient.delete(`/events/${id}`);
}

const getEventsForView = async (): Promise<Event[]> => {
    const events = await getEvents();
    return events.map(mapEventResponseToEvent);
};

export const eventsService = {
    getEvents: getEventsForView,
    getEventById: async (id: number): Promise<Event> => {
        const response = await getEventById(id);
        return mapEventResponseToEvent(response);
    },
    createEvent: async (event: Omit<Event, 'id'>): Promise<Event> => {
        const response = await createEvent({
            name: event.name,
            description: event.description,
            managerId: event.managerCode ?? null,
        });
        return mapEventResponseToEvent(response);
    },
    updateEvent: async (
        id: number,
        event: Partial<Event>,
    ): Promise<Event> => {
        const response = await updateEvent(id, {
            name: event.name ?? '',
            description: event.description ?? '',
            managerId: event.managerCode ?? null,
        });
        return mapEventResponseToEvent(response);
    },
    deleteEvent,
};

export default eventsService;