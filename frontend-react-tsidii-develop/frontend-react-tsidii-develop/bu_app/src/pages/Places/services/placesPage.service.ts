import { placesService } from '../../../services/places.service';

export const placesPageService = {
    async getInputLabelUtilityClasses() {
        return placesService.getPlaces();
    },
};

