import { eventsService } from './events.service';
import { eventPlacesService } from './eventPlaces.service';
import type { UserStats } from '../store/slices/userSlice';

export interface PerformanceData {
    date: string;
    value: number;
}

const EVENT_REGISTRATION_STORAGE_KEY = 'bu_app_event_registrations';
const MILLIS_IN_DAY = 1000 * 60 * 60 * 24;

function normalizeUserKey(userEmail?: string): string {
    return userEmail?.trim().toLowerCase() || 'anonymous';
}

function loadStoredRegistrations(): Record<string, number[]> {
    const raw = localStorage.getItem(EVENT_REGISTRATION_STORAGE_KEY);
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

function saveStoredRegistrations(registrations: Record<string, number[]>): void {
    try {
        localStorage.setItem(EVENT_REGISTRATION_STORAGE_KEY, JSON.stringify(registrations));
    } catch {
        // Ignore storage failures in the browser.
    }
}

export function getRegisteredEventIds(userEmail?: string): number[] {
    const registrations = loadStoredRegistrations();
    return registrations[normalizeUserKey(userEmail)] ?? [];
}

export function saveRegisteredEventIds(userEmail: string, eventIds: number[]): void {
    const registrations = loadStoredRegistrations();
    registrations[normalizeUserKey(userEmail)] = eventIds;
    saveStoredRegistrations(registrations);
}

function parseDate(value?: string): Date | null {
    if (!value) return null;
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? null : date;
}

function buildDistribution(dates: Date[], bucketCount: number, bucketSizeDays: number): number[] {
    const buckets = Array.from({ length: bucketCount }, () => 0);
    const now = new Date();
    now.setHours(0, 0, 0, 0);

    dates.forEach((date) => {
        const diffDays = Math.floor((now.getTime() - date.getTime()) / MILLIS_IN_DAY);
        if (diffDays < 0 || diffDays >= bucketCount * bucketSizeDays) {
            return;
        }

        const bucketIndex = Math.floor(diffDays / bucketSizeDays);
        const normalizedIndex = bucketCount - 1 - bucketIndex;
        buckets[normalizedIndex] += 1;
    });

    return buckets;
}

function normalizeProgress(counts: number[]): number[] {
    const max = Math.max(...counts, 1);
    return counts.map((value) => Math.min(100, Math.max(10, Math.round((value / max) * 80 + 20))));
}

function buildFallbackProgress(totalWorkouts: number, length: number): number[] {
    const base = Math.min(70, 20 + totalWorkouts * 8);
    return Array.from({ length }, (_, index) => Math.min(100, Math.max(15, base - index * 5)));
}

async function loadEventData(userEmail?: string) {
    const registeredIds = getRegisteredEventIds(userEmail);
    const [events, eventPlaces] = await Promise.all([
        eventsService.getEvents(),
        eventPlacesService.getEventPlaces(),
    ]);

    const registeredEvents = events.filter((event) => registeredIds.includes(event.id));
    const registeredPlaces = eventPlaces.filter((place) => registeredIds.includes(place.eventId));

    return {
        registeredEvents,
        registeredPlaces,
    };
}

export const userService = {
    async getStats(userEmail?: string): Promise<UserStats> {
        const { registeredEvents, registeredPlaces } = await loadEventData(userEmail);
        const totalWorkouts = registeredEvents.length;
        const registeredDates = registeredPlaces
            .map((place) => parseDate(place.startDate))
            .filter((date): date is Date => date !== null);

        const weeklyCounts = buildDistribution(registeredDates, 7, 1);
        const monthlyCounts = buildDistribution(registeredDates, 5, 7);

        const weeklyProgress =
            registeredDates.length > 0 ? normalizeProgress(weeklyCounts) : buildFallbackProgress(totalWorkouts, 7);
        const monthlyProgress =
            registeredDates.length > 0 ? normalizeProgress(monthlyCounts) : buildFallbackProgress(totalWorkouts, 5);
        const streak = Math.min(10, totalWorkouts);

        return {
            weeklyProgress,
            monthlyProgress,
            totalWorkouts,
            streak,
        };
    },

    async getWeeklyProgress(userEmail?: string): Promise<PerformanceData[]> {
        const stats = await this.getStats(userEmail);
        return stats.weeklyProgress.map((value, index) => ({ date: `Day ${index + 1}`, value }));
    },

    async getMonthlyProgress(userEmail?: string): Promise<PerformanceData[]> {
        const stats = await this.getStats(userEmail);
        return stats.monthlyProgress.map((value, index) => ({ date: `Week ${index + 1}`, value }));
    },

    async generateProgressReport(
        userName: string,
        stats: UserStats,
        chartImages: { title: string; dataUrl: string }[] = [],
    ): Promise<Blob> {
        const eventRegistrations = stats.totalWorkouts;
        const text = `Progress Report for ${userName}`;

        try {
            const pdfLib = await import('pdf-lib');
            const { PDFDocument, StandardFonts, rgb } = pdfLib;
            const pdfDoc = await PDFDocument.create();
            let page = pdfDoc.addPage([612, 792]);
            const helvetica = await pdfDoc.embedFont(StandardFonts.Helvetica);
            const { height } = page.getSize();

            let y = height - 50;
            page.drawText('Progress Report', { x: 50, y, size: 18, font: helvetica, color: rgb(0, 0, 0) });
            y -= 28;
            page.drawText(`Generated for: ${userName}`, { x: 50, y, size: 12, font: helvetica });
            y -= 20;
            page.drawText(`Date: ${new Date().toLocaleDateString()}`, { x: 50, y, size: 12, font: helvetica });
            y -= 20;
            page.drawText(`Registered events: ${eventRegistrations}`, { x: 50, y, size: 12, font: helvetica });
            y -= 18;
            page.drawText(`Total workouts: ${stats.totalWorkouts}`, { x: 50, y, size: 12, font: helvetica });
            y -= 18;
            page.drawText(`Current streak: ${stats.streak} days`, { x: 50, y, size: 12, font: helvetica });
            y -= 22;
            page.drawText('Weekly progress:', { x: 50, y, size: 12, font: helvetica });
            y -= 16;
            page.drawText(stats.weeklyProgress.join(', '), { x: 60, y, size: 10, font: helvetica });
            y -= 18;
            page.drawText('Monthly progress:', { x: 50, y, size: 12, font: helvetica });
            y -= 16;
            page.drawText(stats.monthlyProgress.join(', '), { x: 60, y, size: 10, font: helvetica });

            for (const chartImage of chartImages) {
                const imagePageHeight = 220;
                if (y - imagePageHeight < 60) {
                    page = pdfDoc.addPage([612, 792]);
                    y = height - 50;
                }

                if (chartImage.dataUrl.startsWith('data:image/png')) {
                    const embedded = await pdfDoc.embedPng(chartImage.dataUrl);
                    const scaled = embedded.scale(Math.min(450 / embedded.width, 1));
                    y -= 24;
                    page.drawText(chartImage.title, { x: 50, y, size: 12, font: helvetica });
                    const imageY = y - scaled.height - 10;
                    page.drawImage(embedded, { x: 50, y: imageY, width: scaled.width, height: scaled.height });
                    y = imageY - 20;
                } else if (chartImage.dataUrl.startsWith('data:image/jpeg') || chartImage.dataUrl.startsWith('data:image/jpg')) {
                    const embedded = await pdfDoc.embedJpg(chartImage.dataUrl);
                    const scaled = embedded.scale(Math.min(450 / embedded.width, 1));
                    y -= 24;
                    page.drawText(chartImage.title, { x: 50, y, size: 12, font: helvetica });
                    const imageY = y - scaled.height - 10;
                    page.drawImage(embedded, { x: 50, y: imageY, width: scaled.width, height: scaled.height });
                    y = imageY - 20;
                }
            }

            const pdfBytes = await pdfDoc.save();
            return new Blob([new Uint8Array(pdfBytes)], { type: 'application/pdf' });
        } catch {
            const fallbackHeader = '%PDF-1.1\n%âãÏÓ\n';
            const fallbackObj1 = '1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n';
            const fallbackObj2 = '2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n';
            const fallbackObj4 = '4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n';
            const fallbackStream = 'BT /F1 18 Tf 50 750 Td (' + escapePdfString(text) + ') Tj ET\n';
            const fallbackObj5 = '5 0 obj\n<< /Length ' + fallbackStream.length + ' >>\nstream\n' + fallbackStream + 'endstream\nendobj\n';
            const fallbackObj3 = '3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>\nendobj\n';
            const fallbackParts = [fallbackHeader, fallbackObj1, fallbackObj2, fallbackObj3, fallbackObj4, fallbackObj5];
            let fallbackOffset = 0;
            const fallbackOffsets: number[] = [];
            for (const part of fallbackParts) {
                fallbackOffsets.push(fallbackOffset);
                fallbackOffset += new TextEncoder().encode(part).length;
            }
            const fallbackXrefStart = fallbackOffset;
            let fallbackXref = 'xref\n0 6\n';
            fallbackXref += '0000000000 65535 f \n';
            for (let i = 0; i < fallbackOffsets.length; i++) {
                const off = fallbackOffsets[i].toString().padStart(10, '0');
                fallbackXref += off + ' 00000 n \n';
            }
            const fallbackTrailer = 'trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n' + fallbackXrefStart + '\n%%EOF\n';
            const fallbackFull = fallbackParts.join('') + fallbackXref + fallbackTrailer;
            return new Blob([new TextEncoder().encode(fallbackFull)], { type: 'application/pdf' });
        }
    },
};

function escapePdfString(input: string): string {
    return input.replace(/\\/g, '\\\\').replace(/\(/g, '\\(').replace(/\)/g, '\\)');
}
