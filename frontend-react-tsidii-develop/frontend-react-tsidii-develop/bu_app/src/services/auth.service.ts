import axiosClient from '../lib/axios/axiosClient';
import type { CurrentUser, LoginResponse } from '../types/api';

export async function loginRequest(username: string, password: string): Promise<string> {
    const response = await axiosClient.post<LoginResponse>('/auth/login', {
        username,
        password,
    });

    const token = response.data.accessToken ?? response.data.token ?? response.data.jwt;

    if (!token) {
        throw new Error('Login response did not include a valid token');
    }

    return token;
}

export async function getCurrentUser(): Promise<CurrentUser> {
    const response = await axiosClient.get<CurrentUser>('/auth/me');

    return response.data;
}
