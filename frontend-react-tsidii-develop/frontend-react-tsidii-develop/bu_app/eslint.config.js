import js from '@eslint/js';
import globals from 'globals';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';
import tseslint from 'typescript-eslint';
import { defineConfig, globalIgnores } from 'eslint/config';

export default defineConfig([
    globalIgnores(['dist', 'node_modules', '.husky']),
    {
        files: ['**/*.{ts,tsx}'],
        extends: [
            js.configs.recommended,
            tseslint.configs.recommended,
            reactHooks.configs.flat.recommended,
            reactRefresh.configs.vite,
        ],
        languageOptions: {
            globals: globals.browser,
        },
        rules: {
            // Error rules
            semi: ['error', 'always'],
            indent: ['error', 4],
            quotes: ['error', 'single', { avoidEscape: true }],
            'no-alert': 'error',
            'no-console': 'error',
            'no-debugger': 'error',
            'no-var': 'error',
            'prefer-const': 'error',
            'eqeqeq': ['error', 'always'],
            'no-implicit-coercion': 'error',
            'no-unused-expressions': 'error',
            'no-empty-function': 'error',

            // TypeScript specific rules
            '@typescript-eslint/no-explicit-any': 'error',
            '@typescript-eslint/no-unused-vars': [
                'error',
                {
                    argsIgnorePattern: '^_',
                    varsIgnorePattern: '^_',
                    caughtErrorsIgnorePattern: '^_',
                },
            ],
            '@typescript-eslint/consistent-type-imports': [
                'error',
                {
                    prefer: 'type-imports',
                },
            ],

            // React hooks rules
            'react-hooks/rules-of-hooks': 'error',
            'react-hooks/exhaustive-deps': 'warn',

            // React refresh
            'react-refresh/only-export-components': 'warn',
        },
    },
    {
        files: ['**/*.js', '**/*.jsx'],
        extends: [js.configs.recommended],
        languageOptions: {
            globals: globals.browser,
        },
        rules: {
            'no-console': 'error',
            'no-var': 'error',
            'prefer-const': 'error',
        },
    },
]);
