import React from 'react';
import { Box, Card, CardContent, Typography, Stack, LinearProgress } from '@mui/material';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import FitnessCenterIcon from '@mui/icons-material/FitnessCenter';
import LocalFireDepartmentIcon from '@mui/icons-material/LocalFireDepartment';

interface StatCardProps {
    title: string;
    value: string | number;
    icon: React.ReactNode;
    subtext?: string;
    progress?: number;
}

const StatCard: React.FC<StatCardProps> = ({ title, value, icon, subtext, progress }) => {
    return (
        <Card>
            <CardContent>
                <Stack spacing={1}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        {icon}
                        <Typography color="textSecondary" variant="body2">
                            {title}
                        </Typography>
                    </Box>
                    <Typography variant="h5">{value}</Typography>
                    {subtext && (
                        <Typography variant="caption" color="textSecondary">
                            {subtext}
                        </Typography>
                    )}
                    {progress !== undefined && (
                        <LinearProgress variant="determinate" value={progress} />
                    )}
                </Stack>
            </CardContent>
        </Card>
    );
};

interface StatsOverviewProps {
    totalWorkouts: number;
    currentStreak: number;
    weeklyTarget: number;
    weeklyProgress: number;
}

export const StatsOverview: React.FC<StatsOverviewProps> = ({
    totalWorkouts,
    currentStreak,
    weeklyTarget,
    weeklyProgress,
}) => {
    const formattedWeeklyProgress = Number(weeklyProgress).toFixed(2);
    const formattedWeeklyTarget = Number(weeklyTarget).toFixed(2);
    const progressPercentage = (weeklyProgress / weeklyTarget) * 100;
    const formattedProgressPercentage = Number(progressPercentage).toFixed(2);

    return (
        <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr', md: '1fr 1fr 1fr 1fr' }, gap: 2 }}>
            <StatCard
                title="Total Workouts"
                value={totalWorkouts}
                icon={<FitnessCenterIcon color="primary" />}
                subtext="All time"
            />
            <StatCard
                title="Current Streak"
                value={`${currentStreak} days`}
                icon={<LocalFireDepartmentIcon sx={{ color: '#ff9800' }} />}
                subtext="Keep it up!"
            />
            <StatCard
                title="Weekly Target"
                value={`${formattedWeeklyProgress}/${formattedWeeklyTarget}`}
                icon={<TrendingUpIcon color="success" />}
                progress={Number(formattedProgressPercentage)}
                subtext={`${formattedProgressPercentage}% complete`}
            />
        </Box>
    );
};
