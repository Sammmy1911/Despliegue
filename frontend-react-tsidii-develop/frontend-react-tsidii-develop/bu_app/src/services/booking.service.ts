const BOOKING_STORAGE_KEY = 'bu_app_place_bookings';

function normalizeUserKey(userEmail?: string): string {
    return userEmail?.trim().toLowerCase() || 'anonymous';
}

function loadStoredBookings(): Record<string, number[]> {
    const raw = localStorage.getItem(BOOKING_STORAGE_KEY);
    if (!raw) {
        return {};
    }

    try {
        const parsed = JSON.parse(raw);
        return typeof parsed === 'object' && parsed !== null ? (parsed as Record<string, number[]>) : {};
    } catch {
        return {};
    }
}

function saveStoredBookings(bookings: Record<string, number[]>): void {
    try {
        localStorage.setItem(BOOKING_STORAGE_KEY, JSON.stringify(bookings));
    } catch {
        // Ignore storage failures.
    }
}

export const bookingService = {
    getBookedPlaceIds(userEmail?: string): number[] {
        const bookings = loadStoredBookings();
        return bookings[normalizeUserKey(userEmail)] ?? [];
    },

    saveBookedPlaceIds(userEmail: string, placeIds: number[]): void {
        const bookings = loadStoredBookings();
        bookings[normalizeUserKey(userEmail)] = placeIds;
        saveStoredBookings(bookings);
    },
};
