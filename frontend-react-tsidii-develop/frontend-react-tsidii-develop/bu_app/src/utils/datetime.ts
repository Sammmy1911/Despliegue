export function toIsoLocalDateTime(value: string, endOfDay = false): string {
    if (!value) {
        return value;
    }

    if (value.includes('T')) {
        return value;
    }

    return `${value}T${endOfDay ? '23:59:59' : '00:00:00'}`;
}
