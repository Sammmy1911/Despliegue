import axiosClient from '../lib/axios/axiosClient';

export async function createAlert(payload: { message: string; trainerId?: number; traineeId?: number; sendDate?: string }) {
    const body = {
        message: payload.message,
        trainerId: payload.trainerId,
        traineeId: payload.traineeId,
    };

    const resp = await axiosClient.post('/alerts', body);
    return resp.data;
}

export async function getAllAlerts() {
    const resp = await axiosClient.get('/alerts/all');
    return resp.data;
}

export async function deleteAlert(id: number) {
    await axiosClient.delete(`/alerts/${id}`);
}

export default { createAlert, getAllAlerts, deleteAlert };
