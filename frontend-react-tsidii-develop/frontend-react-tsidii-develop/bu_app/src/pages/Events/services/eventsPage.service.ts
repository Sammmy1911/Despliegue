import { eventsService } from '../../../services/events.service';

export const eventsPageService = {
    async getEvents() {
        return eventsService.getEvents();
    },
};

