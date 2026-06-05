import React from 'react';
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';
import { Button, CircularProgress, Box } from '@mui/material';
import FileDownloadIcon from '@mui/icons-material/FileDownload';

interface PDFReportProps {
    fileName: string;
    reportContent: string | HTMLElement;
    buttonLabel?: string;
    loading?: boolean;
    variant?: 'contained' | 'outlined' | 'text';
}

export const PDFReport: React.FC<PDFReportProps> = ({
    fileName,
    reportContent,
    buttonLabel = 'Download PDF',
    loading = false,
    variant = 'contained',
}): React.ReactElement => {
    const handleDownload = async (): Promise<void> => {
        try {
            let canvas: HTMLCanvasElement;

            if (typeof reportContent === 'string') {
                const tempDiv = document.createElement('div');
                tempDiv.innerHTML = reportContent;
                tempDiv.style.position = 'absolute';
                tempDiv.style.left = '-9999px';
                document.body.appendChild(tempDiv);

                canvas = await html2canvas(tempDiv);
                document.body.removeChild(tempDiv);
            } else {
                canvas = await html2canvas(reportContent);
            }

            const imgData = canvas.toDataURL('image/png');
            const pdf = new jsPDF({
                orientation: canvas.width > canvas.height ? 'landscape' : 'portrait',
                unit: 'mm',
                format: 'a4',
            });

            const pdfWidth = pdf.internal.pageSize.getWidth();
            const pdfHeight = pdf.internal.pageSize.getHeight();

            const imgWidth = pdfWidth - 20;
            const imgHeight = (canvas.height * imgWidth) / canvas.width;

            let heightLeft = imgHeight;
            let position = 10;

            pdf.addImage(imgData, 'PNG', 10, position, imgWidth, imgHeight);
            heightLeft -= pdfHeight - 20;

            while (heightLeft >= 0) {
                position = heightLeft - imgHeight + 10;
                pdf.addPage();
                pdf.addImage(imgData, 'PNG', 10, position, imgWidth, imgHeight);
                heightLeft -= pdfHeight - 20;
            }

            pdf.save(`${fileName}.pdf`);
        } catch {
            // Silent error handling for PDF generation
        }
    };

    return (
        <Box sx={{ position: 'relative' }}>
            <Button
                variant={variant}
                color="primary"
                onClick={() => void handleDownload()}
                disabled={loading}
                startIcon={loading ? <CircularProgress size={20} /> : <FileDownloadIcon />}
            >
                {buttonLabel}
            </Button>
        </Box>
    );
};

