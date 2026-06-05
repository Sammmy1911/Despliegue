-- Sample data for BU App entities (Fixed schema)
-- Insert order: enums -> relations -> mains -> dependents with exact FK columns

-- 1. Roles
INSERT INTO Roles (type) VALUES ('ADMIN'),('TRAINER'),('TRAINEE');

-- 2. Permissions
INSERT INTO Permissions (name) VALUES
('READ_USER'),
('CREATE_USER'),
('UPDATE_USER'),
('DELETE_USER'),
('ASSIGN_ROLE_USER'),
('REMOVE_ROLE_USER'),
('READ_ROLE'),
('CREATE_ROLE'),
('DELETE_ROLE'),
('ASSIGN_PERMISSION_ROLE'),
('CREATE_ROUTINE'),
('VIEW_PROGRESS');

-- 3. Status
INSERT INTO Status (status) VALUES ('ACTIVE'),('INACTIVE');

-- 4. ExerciseType
INSERT INTO Exercise_Types (name) VALUES 
('CARDIO'),('STRENGTH'),('FLEXIBILITY'),('HIIT');

-- 5. ExerciseDifficulty
INSERT INTO Exercise_Difficulties (name) VALUES 
('EASY'),('MEDIUM'),('HARD'),('EXPERT');

-- 6. ProgressType
INSERT INTO Progress_Types (type) VALUES 
('EXCELLENT'),('GOOD'),('AVERAGE'),('POOR');

-- 7. StressLevel
INSERT INTO Stress_Levels (level) VALUES 
('LOW'),('MEDIUM'),('HIGH'),('EXTREME');

-- 8. Role_Permissions
INSERT INTO Role_Permissions (role_id, permission_id) VALUES 
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),
(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),
(2,1),(2,11),(2,12),
(3,1),(3,12);

-- 9. Places
INSERT INTO Places (name, status_id) VALUES 
('Gym Icesi', 1),('Parque Espiritu Santo', 1),('Sala Pesas Uni', 1),('Piscina Olimpica', 2);

-- 10. Users (6 users: 1 admin, 2 trainers, 3 trainees)
INSERT INTO Users (name, email, password, date_of_birth, sex, role_id, trainer_id) VALUES 
('Admin Master', 'admin@icesi.edu.co', '$2a$10$gO9KUkyAjno9VBj9XZPmN.vLku2ZwmTXJP3.MTojSGrw6sSCtm3qG', '1990-01-01', 'M', 1, NULL), -- password hashed for 'admin123' USAR ESTE PARA PROBAR LOGIN
('Juan Perez', 'juan.trainer@icesi.edu.co', '$2a$10$I5KujVpHR7VFPN5chXWbqO0r9hKZjoj9byhcHWYNhJyZt1HbQcHUy', '1985-05-15', 'M', 2, NULL), -- password hashed for 'trainer123'
('Maria Lopez', 'maria.trainer@icesi.edu.co', '$2a$10$I5KujVpHR7VFPN5chXWbqO0r9hKZjoj9byhcHWYNhJyZt1HbQcHUy', '1988-08-20', 'F', 2, NULL), -- password hashed for 'trainer123'
('Carlos Student', 'carlos@icesi.edu.co', '$2a$10$rLaOnxI6HNXXmcgEfbJYROb/V82x2zu4VRNIMOVgmOGhMkJnQLfii', '2000-03-10', 'M', 3, 2), -- password hashed for 'pass123'
('Ana Garcia', 'ana@icesi.edu.co', '$2a$10$rLaOnxI6HNXXmcgEfbJYROb/V82x2zu4VRNIMOVgmOGhMkJnQLfii', '1999-12-05', 'F', 3, 2), -- password hashed for 'pass123'
('Luis Ramirez', 'luis@icesi.edu.co', '$2a$10$rLaOnxI6HNXXmcgEfbJYROb/V82x2zu4VRNIMOVgmOGhMkJnQLfii', '2001-07-22', 'M', 3, 3); -- password hashed for 'pass123'



INSERT INTO Users (name, email, password, date_of_birth, sex, role_id) VALUES 
('AdminTest','admin@test1.com','$2a$10$Dow1ZpZ1Zq8h3JvYv8eZ4uQ6gGQ8H9F2nPjF4X9lP5kU5H3j2Yz3W','2000-01-01','M',1);
-- 11. Exercises (predefined + custom)
INSERT INTO Exercises (name, length, description, video, is_custom, difficulty_id, type_id, owner_id) VALUES 
('Running 5km', 30, 'Correr 5km a ritmo moderado', X'00', FALSE, 1, 1, NULL),
('Flexiones', 15, 'Series de 3x20 flexiones', X'00', FALSE, 2, 2, NULL),
('Yoga Basico', 20, 'Poses basicas de yoga', X'00', FALSE, 1, 3, NULL),
('Sentadillas', 25, '3x15 sentadillas con peso corporal', X'00', FALSE, 3, 2, NULL),
('Burpees HIIT', 10, '10 min de burpees intensos', X'00', FALSE, 4, 4, NULL),
('Plan Personal de Ana', 18, 'Rutina personalizada para movilidad y fuerza', X'00', TRUE, 2, 3, 5);

-- 12. Routines (4 routines)
INSERT INTO Routines (name, trainer_id, trainee_id) VALUES 
('Rutina Cardio Semanal', 2, 4),
('Fuerza Iniciante', 2, 4),
('Yoga Mensual', 3, 5),
('HIIT Avanzado', 3, 6);

-- 13. RoutineExercise (6 joins)
INSERT INTO Routines_Exercises (routine_id, exercise_id) VALUES 
(1,1),(1,2),(2,2),(2,4),(3,3),(4,5);

-- 14. Events (3 events)
INSERT INTO Events (name, description, manager_id) VALUES 
('Taller Cardio', 'Sesion grupal de cardio', 2),
('Competicion Fuerza', 'Torneo de levantamiento', 1),
('Clase Yoga Gratis', 'Yoga para todos', 3);

-- 15. EventPlace (3 joins + dates)
INSERT INTO Events_Places (event_id, place_id, start_date, end_date) VALUES 
(1,1, '2024-10-10 09:00:00', '2024-10-10 10:00:00'),
(2,3, '2024-10-15 15:00:00', '2024-10-15 18:00:00'),
(3,2, '2024-10-20 18:00:00', '2024-10-20 19:00:00');

-- 16. Progress (3 progresses)
INSERT INTO Progresses (repetitions, time, stress_level_id, type_id, trainee_id, routine_id, performed_at) VALUES 
(50, '30min', 1, 1, 4, 1, '2024-10-04 10:00:00'),
(20, '15min', 2, 2, 4, 2, '2024-10-05 09:30:00'),
(30, '20min', 3, 3, 5, 3, '2024-10-06 08:45:00');

-- 17. Recommendations (FK: trainer_id, progress_id)
INSERT INTO Recommendations (description, trainer_id, progress_id) VALUES 
('Aumentar cardio diario', 2, 1),
('Mejorar flexibilidad', 3, 3),
('Reducir stress con yoga', 2, 2);

-- 18. Alerts (FK trainer_id, trainee_id)
INSERT INTO Alerts (send_date, message, trainer_id, trainee_id) VALUES 
('2024-10-04 10:00:00', 'Recordar rutina semanal', 2, 4),
('2024-10-05 11:00:00', 'Progreso lento, ajustar plan', 3, 5);
