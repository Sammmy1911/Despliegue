package com.icesi.bu_app.mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;

import com.icesi.bu_app.model.Exercise;
import com.icesi.bu_app.model.Permission;
import com.icesi.bu_app.model.Role;
import com.icesi.bu_app.model.User;
import com.icesi.bu_app.repository.IPermissionRepository;
import com.icesi.bu_app.repository.IRolePermissionRepository;
import com.icesi.bu_app.security.CustomUserDetails;
import com.icesi.bu_app.service.IExerciseService;
import com.icesi.bu_app.service.IProgressService;
import com.icesi.bu_app.service.IRoutineExerciseService;
import com.icesi.bu_app.service.IRoutineService;
import com.icesi.bu_app.service.IUserService;
import com.icesi.bu_app.service.impl.PermissionService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class PermissionServiceTest {

    // Mocks del repositorio que PermissionService necesita
    @Mock
    private IPermissionRepository permissionRepository;

    @Mock
    private IUserService userService;

    @Mock
    private IRolePermissionRepository rolePermissionRepository;

    @Mock
    private IRoutineService routineService;

    @Mock
    private IProgressService progressService;

    @Mock
    private IExerciseService exerciseService;

    @Mock
    private IRoutineExerciseService routineExerciseService;

    @InjectMocks
    private PermissionService permissionService;

    private Permission permission;

    // Setup inicial
    @BeforeEach
    void setUp() {
        permission = new Permission();
        permission.setId(1);
        permission.setName("READ");
    }

    // Prueba para findAll (consulta)
    @Test
    void testFindAll() {
        when(permissionRepository.findAll()).thenReturn(List.of(permission));
        List<Permission> result = permissionService.findAll();
        assertEquals(1, result.size());
        assertEquals("READ", result.get(0).getName());
    }

    // Prueba para findById (consulta por id)
    @Test
    void testFindById() {
        when(permissionRepository.findById(1)).thenReturn(Optional.of(permission));
        Permission result = permissionService.findById(1);
        assertNotNull(result);
        assertEquals("READ", result.getName());
    }

    // Prueba para save (inserción)
    @Test
    void testSave() {
        when(permissionRepository.save(any(Permission.class))).thenReturn(permission);
        Permission saved = permissionService.save(permission);
        assertNotNull(saved);
        assertEquals("READ", saved.getName());
    }

    // Prueba para update (actualización)
    @Test
    void testUpdate() {
        // Datos de actualización
        Permission updatedData = new Permission();
        updatedData.setName("WRITE");

        // Simulamos que el permiso existe
        when(permissionRepository.findById(1)).thenReturn(Optional.of(permission));
        when(permissionRepository.save(any(Permission.class))).thenReturn(updatedData);

        Permission result = permissionService.update(1, updatedData);
        assertEquals("WRITE", result.getName());
    }

    @Test
    void testDelete_Success() {
        when(permissionRepository.findById(1)).thenReturn(Optional.of(permission));
        doNothing().when(permissionRepository).deleteById(1);
        
        permissionService.delete(1);
        
        verify(permissionRepository).findById(1);
        verify(permissionRepository).deleteById(1);
    }

    @Test
    void testDelete_NotFound() {
        when(permissionRepository.findById(99)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> permissionService.delete(99));
        verify(permissionRepository, never()).deleteById(any());
    }

    @Test
    void trainerCanManageCustomExerciseOwnedBySelf() {
        Authentication trainerAuth = authFor(10, "TRAINER");

        boolean result = permissionService.canManageExercise(trainerAuth, 10, true);

        assertTrue(result);
        verify(userService, never()).findById(any());
    }

    @Test
    void trainerCanManageCustomExerciseForAssignedTrainee() {
        Authentication trainerAuth = authFor(10, "TRAINER");
        User trainee = new User();
        trainee.setCode(20);
        User trainer = new User();
        trainer.setCode(10);
        trainee.setTrainer(trainer);
        when(userService.findById(20)).thenReturn(trainee);

        boolean result = permissionService.canManageExercise(trainerAuth, 20, true);

        assertTrue(result);
    }

    @Test
    void trainerCanModifyCustomExerciseOfAssignedTrainee() {
        Authentication trainerAuth = authFor(10, "TRAINER");

        User trainee = new User();
        trainee.setCode(20);
        User trainer = new User();
        trainer.setCode(10);
        trainee.setTrainer(trainer);

        Exercise exercise = new Exercise();
        exercise.setId(7);
        exercise.setCustom(true);
        exercise.setOwner(trainee);

        when(exerciseService.findById(7)).thenReturn(exercise);

        boolean result = permissionService.canModifyExercise(trainerAuth, 7);

        assertTrue(result);
    }

    @Test
    void trainerCanManageProgressForAssignedTrainee() {
        Authentication trainerAuth = authFor(10, "TRAINER");
        User trainee = new User();
        trainee.setCode(20);
        User trainer = new User();
        trainer.setCode(10);
        trainee.setTrainer(trainer);
        when(userService.findById(20)).thenReturn(trainee);

        boolean result = permissionService.canManageProgress(trainerAuth, 20);

        assertTrue(result);
    }

    @Test
    void trainerCannotManageProgressForUnassignedTrainee() {
        Authentication trainerAuth = authFor(10, "TRAINER");
        User trainee = new User();
        trainee.setCode(30);
        User anotherTrainer = new User();
        anotherTrainer.setCode(99);
        trainee.setTrainer(anotherTrainer);
        when(userService.findById(30)).thenReturn(trainee);

        boolean result = permissionService.canManageProgress(trainerAuth, 30);

        assertFalse(result);
    }

    @Test
    void trainerCanModifyProgressForAssignedTrainee() {
        Authentication trainerAuth = authFor(10, "TRAINER");

        User trainee = new User();
        trainee.setCode(20);
        User trainer = new User();
        trainer.setCode(10);
        trainee.setTrainer(trainer);

        com.icesi.bu_app.model.Progress progress = new com.icesi.bu_app.model.Progress();
        progress.setId(11);
        progress.setTrainee(trainee);

        when(progressService.findById(11)).thenReturn(progress);
        when(userService.findById(20)).thenReturn(trainee);

        boolean result = permissionService.canModifyProgress(trainerAuth, 11);

        assertTrue(result);
    }

    private Authentication authFor(Integer userCode, String roleType) {
        Role role = new Role();
        role.setType(roleType);

        User user = new User();
        user.setCode(userCode);
        user.setRole(role);
        user.setEmail("user" + userCode + "@mail.com");

        CustomUserDetails principal = new CustomUserDetails(user);
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                Set.of(new SimpleGrantedAuthority("ROLE_" + roleType)));
    }
}