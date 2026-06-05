import { Box, CircularProgress } from '@mui/material';
import { lazy, Suspense, type ComponentType, type ReactNode } from 'react';
import { createBrowserRouter } from 'react-router-dom';
import ProtectedRoute from '../components/ProtectedRoute';

function withSuspense(element: ReactNode) {
    return (
        <Suspense
            fallback={
                <Box sx={{ minHeight: '100vh', display: 'grid', placeItems: 'center' }}>
                    <CircularProgress />
                </Box>
            }
        >
            {element}
        </Suspense>
    );
}

function lazyElement(importer: () => Promise<{ default: ComponentType<Record<string, never>> }>): ReactNode {
    const LazyComponent = lazy(importer);

    return withSuspense(<LazyComponent />);
}

const router = createBrowserRouter(
    [
        {
            path: '/',
            element: lazyElement(() => import('../pages/Landing/Landing.tsx')),
        },
        {
            path: '/auth',
            children: [
                {
                    index: true,
                    element: lazyElement(() => import('../pages/Login/Login.tsx')),
                },
                {
                    path: 'login',
                    element: lazyElement(() => import('../pages/Login/Login.tsx')),
                },
            ],
        },
        {
            path: '/app',
            element: <ProtectedRoute />,
            children: [
                {
                    path: '',
                    element: lazyElement(() => import('../components/AppShell')),
                    children: [
                        {
                            index: true,
                            element: lazyElement(() => import('../pages/DashboardPage')),
                        },
                        {
                            path: 'exercises',
                            element: lazyElement(() => import('../pages/Exercises/ExercisesPage')),
                        },
                        {
                            path: 'routines',
                            element: lazyElement(() => import('../pages/Routines/RoutinesPage')),
                        },
                        {
                            path: 'routines/new',
                            element: lazyElement(() => import('../pages/Routines/CreateRoutine')),
                        },
                        {
                            path: 'progress',
                            element: lazyElement(() => import('../pages/Progress/ProgressPage')),
                        },
                        {
                            path: 'history',
                            element: lazyElement(() => import('../pages/History/History')),
                        },
                        {
                            path: 'recommendations',
                            element: lazyElement(() => import('../pages/Recommendations/Recommendations')),
                        },
                        {
                            path: 'trainer',
                            element: lazyElement(() => import('../pages/Trainer/TrainerDashboard')),
                        },
                        {
                            path: 'admin',
                            element: lazyElement(() => import('../pages/Admin/AdminPanel')),
                            children: [
                                {
                                    path: 'events',
                                    element: lazyElement(() => import('../pages/Admin/AdminEventsPage')),
                                },
                            ],
                        },
                    ],
                },
            ],
        },
        {
            path: '/events',
            element: <ProtectedRoute />,
            children: [
                {
                    path: '',
                    element: lazyElement(() => import('../components/AppShell')),
                    children: [
                        {
                            index: true,
                            element: lazyElement(() => import('../pages/Events/EventsPage.tsx')),
                        },
                    ],
                },
            ],
        },
    ],
    { basename: '/bu-app' }
);

export default router;
