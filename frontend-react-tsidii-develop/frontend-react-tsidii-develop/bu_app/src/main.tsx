import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { Provider } from 'react-redux';
import { PersistGate } from 'redux-persist/integration/react';
import { AuthProvider } from './context/AuthContext';
import { RouterProvider } from 'react-router-dom';
import router from './router/Router';
import { CssBaseline, ThemeProvider, createTheme } from '@mui/material';
import { store, persistor } from './store/store';

const theme = createTheme({
    palette: {
        primary: {
            main: '#0057B8',
        },
        secondary: {
            main: '#17A589',
        },
    },
    shape: {
        borderRadius: 12,
    },
});

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <Provider store={store}>
            <PersistGate loading={null} persistor={persistor}>
                <ThemeProvider theme={theme}>
                    <CssBaseline />
                    <AuthProvider>
                        <RouterProvider router={router} />
                    </AuthProvider>
                </ThemeProvider>
            </PersistGate>
        </Provider>
    </StrictMode>
);
