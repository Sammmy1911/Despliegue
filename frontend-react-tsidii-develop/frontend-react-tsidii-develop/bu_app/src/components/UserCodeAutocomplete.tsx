import { Autocomplete, Box, TextField, Typography } from '@mui/material';

export type UserLookupOption = {
    code: number;
    name: string;
};

type UserCodeAutocompleteProps = {
    label: string;
    codeValue: string;
    options: UserLookupOption[];
    disabled?: boolean;
    onCodeChange: (value: string) => void;
};

function normalizeText(value: string): string {
    return value
        .normalize('NFD')
        .replace(/\p{Diacritic}/gu, '')
        .toLowerCase()
        .trim();
}

export default function UserCodeAutocomplete({
    label,
    codeValue,
    options,
    disabled = false,
    onCodeChange,
}: UserCodeAutocompleteProps) {
    const selectedOption = options.find((option) => String(option.code) === codeValue) ?? null;

    return (
        <Autocomplete
            options={options}
            value={selectedOption}
            disabled={disabled}
            autoHighlight
            onChange={(_, value) => {
                onCodeChange(value ? String(value.code) : '');
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
                    const labelText = `${item.name} - ${item.code}`;

                    return normalizeText(labelText).includes(normalizedInput);
                });
            }}
            getOptionLabel={(option) => `${option.name} - ${option.code}`}
            isOptionEqualToValue={(option, value) => option.code === value.code}
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
                        <Typography variant="body2" sx={{ fontWeight: 500 }} noWrap>
                            {option.name}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                            {option.code}
                        </Typography>
                    </Box>
                </li>
            )}
            renderInput={(params) => (
                <TextField
                    {...params}
                    label={label}
                    placeholder="Escribe nombre o código"
                />
            )}
        />
    );
}
