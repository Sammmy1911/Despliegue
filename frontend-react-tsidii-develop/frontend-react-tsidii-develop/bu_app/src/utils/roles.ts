export function normalizeRole(rawRole: string | undefined | null): string {
    if (!rawRole) {
        return '';
    }

    return rawRole.replace('ROLE_', '').toUpperCase();
}
