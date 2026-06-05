import { useEffect, useMemo, useState } from 'react';
import { IconButton, Badge, Menu, MenuItem, ListItemText, IconButton as SmallIconButton } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import NotificationClient, { type AlertMessage } from '../lib/ws/notificationClient';
import { useAuth } from '../context/useAuth';
import alertsService from '../services/alerts.service';

export default function NotificationsBell() {
    const { user } = useAuth();
    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
    const [items, setItems] = useState<AlertMessage[]>([]);

    useEffect(() => {
        const client = new NotificationClient();

        const handler = (payload: AlertMessage) => {
            if (payload?.type === 'poll') {
                const data = Array.isArray(payload.data) ? payload.data : [];
                // filter to current user (trainee)
                const filtered = data.filter((d: AlertMessage) => user && d.traineeCode === user.code);
                setItems(filtered as AlertMessage[]);
            } else {
                const data = (payload.data ?? payload) as AlertMessage;
                if (user && data.traineeCode === user.code) {
                    setItems((cur) => [data, ...cur]);
                }
            }
        };

        client.onMessage(handler);

        // also fetch initial list (in case polling disabled)
        void alertsService.getAllAlerts().then((arr: AlertMessage[]) => {
            if (user) {
                const filtered = arr.filter((d) => d.traineeCode === user.code);
                setItems(filtered);
            }
        }).catch(() => {
            // ignore
        });

        return () => {
            client.offMessage(handler);
            client.dispose();
        };
    }, [user]);

    const unread = useMemo(() => items.length, [items]);

    const handleDelete = async (id?: number) => {
        if (!id) return;
        try {
            await alertsService.deleteAlert(id);
            setItems((cur) => cur.filter((it) => it.id !== id));
        } catch {
            // ignore
        }
    };

    return (
        <>
            <IconButton
                color="inherit"
                onClick={(event: React.MouseEvent<HTMLButtonElement>) => setAnchorEl(event.currentTarget)}
            >
                <Badge badgeContent={unread} color="error">
                    <svg width="24" height="24" viewBox="0 0 24 24" aria-hidden>
                        <path d="M12 22c1.1 0 2-.9 2-2h-4a2 2 0 0 0 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4a1.5 1.5 0 0 0-3 0v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z" />
                    </svg>
                </Badge>
            </IconButton>

            <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={() => setAnchorEl(null)}>
                {items.length === 0 && <MenuItem>No hay notificaciones</MenuItem>}

                {items.map((it, idx) => (
                    <MenuItem key={idx} onClick={() => setAnchorEl(null)}>
                        <ListItemText primary={it.title ?? it.message ?? 'Alerta'} secondary={it.body ?? undefined} />
                        <SmallIconButton size="small" onClick={(e) => { e.stopPropagation(); handleDelete(it.id); }}>
                            <DeleteIcon fontSize="small" />
                        </SmallIconButton>
                    </MenuItem>
                ))}
            </Menu>
        </>
    );
}
