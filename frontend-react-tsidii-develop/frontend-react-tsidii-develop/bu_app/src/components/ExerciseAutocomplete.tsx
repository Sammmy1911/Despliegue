import { Autocomplete, Box, TextField, Typography } from '@mui/material';

export type ExerciseLookupOption = {
    id: number;
    name: string;
    typeName?: string;
    difficultyName?: string;
};

type ExerciseAutocompleteProps = {
    label: string;
    value: string;
    options: ExerciseLookupOption[];
    disabled?: boolean;
    fullWidth?: boolean;
    onChange: (value: string) => void;
};

function normalizeText(value: string): string {
    return value
        .normalize('NFD')
        .replace(/\p{Diacritic}/gu, '')
        .toLowerCase()
        .trim();
}

export default function ExerciseAutocomplete({
    label,
    value,
    options,
    disabled = false,
    fullWidth = true,
    onChange,
}: ExerciseAutocompleteProps) {
    const selectedOption = options.find((option) => String(option.id) === value) ?? null;

    return (
        <Autocomplete
            options={options}
            value={selectedOption}
            disabled={disabled}
            fullWidth={fullWidth}
            autoHighlight
            onChange={(_, selected) => {
                onChange(selected ? String(selected.id) : '');
            }}
            slotProps={{
                listbox: {
                    sx: {
                        maxHeight: 240,
                        py: 0,
                    },
                },
            }}
            filterOptions={(items, state) => {
                const normalizedInput = normalizeText(state.inputValue);

                if (!normalizedInput) {
                    return items;
                }

                return items.filter((item) => {
                    const labelText = `${item.name} - ${item.id} ${item.typeName ?? ''} ${item.difficultyName ?? ''}`;

                    return normalizeText(labelText).includes(normalizedInput);
                });
            }}
            getOptionLabel={(option) => `${option.name} - ${option.id}`}
            isOptionEqualToValue={(option, selected) => option.id === selected.id}
            renderOption={(props, option) => (
                <li {...props}>
                    <Box
                        sx={{
                            width: '100%',
                            display: 'grid',
                            gridTemplateColumns: '1fr auto',
                            gap: 1,
                            alignItems: 'center',
                        }}
                    >
                        <Box sx={{ overflow: 'hidden' }}>
                            <Typography variant="body2" sx={{ fontWeight: 500 }} noWrap>
                                {option.name}
                            </Typography>
                            <Typography variant="caption" color="text.secondary" noWrap>
                                {[option.typeName, option.difficultyName].filter(Boolean).join(' • ')}
                            </Typography>
                        </Box>
                        <Typography variant="body2" color="text.secondary">
                            {option.id}
                        </Typography>
                    </Box>
                </li>
            )}
            renderInput={(params) => <TextField {...params} label={label} placeholder="Escribe nombre, tipo o id" />}
        />
    );
}
