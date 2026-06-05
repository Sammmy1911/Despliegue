package com.icesi.bu_app.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.icesi.bu_app.model.Role;
import com.icesi.bu_app.model.User;
import com.icesi.bu_app.repository.IUserRepository;
import com.icesi.bu_app.service.IRoleService;
import com.icesi.bu_app.service.impl.UserService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {
    // Mocks de los repositorios y servicios que el UserService necesita para
    // funcionar,
    // esto es necesario para poder probar el UserService de manera aislada,
    // sin depender de otros servicios o repositorios.
    @Mock
    private IUserRepository userRepository;
    @Mock
    private IRoleService roleService;
    @InjectMocks
    private UserService userService;
    private User user;
    private Role role;
    @Mock
    private PasswordEncoder passwordEncoder;
    // Setup Corregido
    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1);
        role.setType("Admin");

        user = new User();
        user.setCode(1);
        user.setName("John Doe");
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setBirthDate(Date.valueOf("1990-01-01"));
        user.setSex("M");
        user.setRole(role);
    }

    @Test
    void testGetAllUsers() {
        Page<User> page = new PageImpl<>(List.of(user));
        // Uso el when para simular el comportamiento del repositorio, le digo que
        // cuando
        // se llame al método findAll con cualquier Pageable,
        // devuelva la página que creé con el usuario de prueba.
        when(userRepository.findByIsDeletedFalse(any(Pageable.class))).thenReturn(page);
        Page<User> result = userService.findAll(0, 10);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testFindById() {
        when(userRepository.findByCodeAndIsDeletedFalse(1)).thenReturn(Optional.of(user));
        User result = userService.findById(1);
        assertNotNull(result);
    }

    @Test
    void testSave() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
        when(roleService.findById(1)).thenReturn(role);
        when(userRepository.save(any(User.class))).thenReturn(user);
        User saved = userService.save(user);
        assertNotNull(saved);
        assertTrue(saved.isDeleted() == false);
    }

    @Test
    void testUpdate() {
        when(userRepository.findByCodeAndIsDeletedFalse(1)).thenReturn(Optional.of(user));
        when(roleService.findById(1)).thenReturn(role);
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updated = new User();
        updated.setName("New Name");
        updated.setEmail("new@test.com");
        updated.setPassword("123");
        updated.setBirthDate(Date.valueOf("2000-01-01"));
        updated.setSex("F");

        Role newRole = new Role();
        newRole.setId(1);
        updated.setRole(newRole);

        User result = userService.update(1, updated);

        assertEquals("New Name", result.getName());
        verify(passwordEncoder).encode(any(String.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRemove() {
        when(userRepository.findByCodeAndIsDeletedFalse(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.remove(1);

        assertTrue(user.isDeleted());
        verify(userRepository).save(user);
    }

    @Test
    void testSave_WhenRoleIsNull() {
        user.setRole(null);
        assertThrows(RuntimeException.class, () -> userService.save(user));
        verify(userRepository, never()).save(any());
    }
    
    
    @Test
    void testFindTrainers() {
        List<User> trainers = List.of(user);
        when(userRepository.findByRoleTypeAndIsDeletedFalse("TRAINER")).thenReturn(trainers);
        List<User> result = userService.findTrainers();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findByRoleTypeAndIsDeletedFalse("TRAINER");
    }
    
    
    @Test
    void testFindByEmail() {
        when(userRepository.findByEmailAndIsDeletedFalse("test@test.com")).thenReturn(Optional.of(user));
        User result = userService.findByEmail("test@test.com");
        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
        verify(userRepository).findByEmailAndIsDeletedFalse("test@test.com");
    }
    
    @Test //Basicamente como el findById pero con el email, 
    // es para corroborar que el método del repositorio funciona correctamente
    void testFindTrainers_EmptyList() {
        when(userRepository.findByRoleTypeAndIsDeletedFalse("TRAINER")).thenReturn(List.of());
        List<User> result = userService.findTrainers();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findByRoleTypeAndIsDeletedFalse("TRAINER");
    }


        @Test
    void testFindById_UserNotFound() {
        //en la partecodigo del servicio, el método findById lanza una RuntimeException cuando el usuario no se encuentra
        when(userRepository.findByCodeAndIsDeletedFalse(999)).thenReturn(Optional.empty()); //Caso absurdo, no existe un usuario con id 999
        assertThrows(RuntimeException.class, () -> { //Ok, basicamente lo que estaba intentando es verificar que el método
        //  findById del servicio lanza una excepción cuando el usuario no se encuentra, pero no se me ocurría como hacerlo, 
        // entonces lo hice de esta manera, y funciona, el test pasa correctamente
            userService.findById(999);
        });
        verify(userRepository).findByCodeAndIsDeletedFalse(999);
    }

    @Test
    void testSave_ReactivatesDeletedUser() {
        User deletedUser = new User();
        deletedUser.setCode(5);
        deletedUser.setEmail("test@test.com");
        deletedUser.setDeleted(true);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(deletedUser));
        when(roleService.findById(1)).thenReturn(role);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.save(user);

        assertNotNull(saved);
        assertTrue(!saved.isDeleted());
    }
}