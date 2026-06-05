export type UserRole = 'ADMIN' | 'TRAINER' | 'TRAINEE';

export type CurrentUser = {
    code: number;
    name: string;
    email: string;
    role: string;
    authorities: string[];
    trainerCode: number | null;
};

export type LoginResponse = {
    token?: string;
    accessToken?: string;
    jwt?: string;
};

export type SelectOption = {
    id: number;
    name: string;
};

export type ExerciseResponse = {
    id: number;
    name: string;
    length: number;
    description: string;
    custom: boolean;
    difficultyId: number;
    difficultyName: string;
    typeId: number;
    typeName: string;
    ownerCode: number | null;
    ownerName: string | null;
};

export type ExerciseRequest = {
    name: string;
    length: number;
    description: string;
    custom: boolean;
    difficultyId: number;
    typeId: number;
    ownerCode?: number;
};

export type RoutineResponse = {
    id: number;
    name: string;
    trainerCode: number;
    trainerName: string;
    traineeCode: number;
    traineeName: string;
};

export type RoutineRequest = {
    name: string;
    trainerCode: number;
    traineeCode: number;
};

export type RoutineExerciseResponse = {
    id: number;
    routineId: number;
    exerciseId: number;
};

export type RoutineExerciseRequest = {
    routineId: number;
    exerciseId: number;
};

export type ProgressResponse = {
    id: number;
    repetitions: number;
    time: string;
    stressLevelId: number;
    stressLevelName: string;
    progressTypeId: number;
    progressTypeName: string;
    traineeId: number;
    traineeName: string;
    routineId: number;
    routineName: string;
    performedAt: string;
};

export type ProgressRequest = {
    repetitions: number;
    time: string;
    stressLevelId: number;
    progressTypeId: number;
    traineeId: number;
    routineId: number;
    performedAt: string;
};

export type EventResponse = {
    id: number;
    name: string;
    description: string;
    managerCode: number | null;
    managerName: string | null;
};

export type EventRequest = {
    name: string;
    description: string;
    managerId: number | null;
};

export type AggregatedProgressResponse = {
    period: string;
    totalRepetitions: number;
    entriesCount: number;
};
