import React from 'react';
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend,
} from 'chart.js';
import { Line } from 'react-chartjs-2';
import { Box, Paper, Typography } from '@mui/material';

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend);

interface ProgressChartProps {
    data: number[];
    labels: string[];
    title: string;
}

export const ProgressChart = React.forwardRef<ChartJS<'line', number[], string> | undefined, ProgressChartProps>(
    ({ data, labels, title }, ref) => {
        const chartData = {
            labels,
            datasets: [
                {
                    label: title,
                    data,
                    borderColor: 'rgb(75, 192, 192)',
                    backgroundColor: 'rgba(75, 192, 192, 0.1)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.4,
                },
            ],
        };

        const options = {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'top' as const,
                },
                title: {
                    display: false,
                },
            },
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100,
                },
            },
        };

        return (
            <Paper sx={{ p: 2 }}>
                <Typography variant="h6" gutterBottom>
                    {title}
                </Typography>
                <Box sx={{ height: '300px' }}>
                    <Line ref={ref} data={chartData} options={options} />
                </Box>
            </Paper>
        );
    },
);

ProgressChart.displayName = 'ProgressChart';
