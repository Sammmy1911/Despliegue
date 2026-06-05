import axiosClient from '../lib/axios/axiosClient';

// NOTE: The backend currently exposes MVC user management under /mvc/users but
// not a REST API for users. The endpoints below assume REST endpoints exist
// (e.g. /rest/users, /rest/users/trainers). If they don't exist yet, the
// backend needs a small REST controller to support these calls.

export async function getUsers(page = 0, size = 50) {
    const resp = await axiosClient.get(`/users?page=${page}&size=${size}`);
    return resp.data;
}

export async function getTrainers() {
    const resp = await axiosClient.get('/users/trainers');
    return resp.data;
}

export async function assignTrainerToUser(userId: number, trainerId: number | null) {
    const resp = await axiosClient.put(`/users/${userId}/trainer`, { trainerId });
    return resp.data;
}

export default { getUsers, getTrainers, assignTrainerToUser };
