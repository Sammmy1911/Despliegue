import { configureStore } from '@reduxjs/toolkit';
import { persistStore, persistReducer } from 'redux-persist';
import authReducer from './slices/authSlice';
import eventsReducer from './slices/eventsSlice';
import placesReducer from './slices/placeSlice';
import userReducer from './slices/userSlice';

// Custom localStorage wrapper that returns Promises
const customStorage = {
    getItem: (key: string) => {
        return new Promise((resolve) => {
            try {
                const item = localStorage.getItem(key);
                resolve(item);
            } catch {
                resolve(null);
            }
        });
    },
    setItem: (key: string, value: string) => {
        return new Promise((resolve) => {
            try {
                localStorage.setItem(key, value);
                resolve(undefined);
            } catch {
                resolve(undefined);
            }
        });
    },
    removeItem: (key: string) => {
        return new Promise((resolve) => {
            try {
                localStorage.removeItem(key);
                resolve(undefined);
            } catch {
                resolve(undefined);
            }
        });
    },
};

const persistConfig = {
    key: 'root',
    storage: customStorage,
    whitelist: ['auth'],
};

const persistedAuthReducer = persistReducer(persistConfig, authReducer);

export const store = configureStore({
    reducer: {
        auth: persistedAuthReducer,
        events: eventsReducer,
        places: placesReducer,
        user: userReducer,
    },
    middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({
            serializableCheck: {
                ignoredActions: ['persist/PERSIST', 'persist/REHYDRATE', 'persist/REGISTER'],
                ignoredPaths: ['err'],
            },
        }),
});

export const persistor = persistStore(store);

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
