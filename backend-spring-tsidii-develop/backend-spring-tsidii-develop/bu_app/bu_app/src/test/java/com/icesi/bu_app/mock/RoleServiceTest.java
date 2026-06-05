package com.icesi.bu_app.mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.icesi.bu_app.model.*;
import com.icesi.bu_app.repository.*;
import com.icesi.bu_app.service.impl.RoleService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class RoleServiceTest {
    @Mock
    private IRoleRepository roleRepository;
    @Mock
    private IRolePermissionRepository rolePermissionRepository;
    @Mock
    private IPermissionRepository permissionRepository;
    @InjectMocks
    private RoleService roleService;
    private Role role;
    private Permission permission;
//Toco cambiar 
    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1);
        role.setType("ADMIN");
        role.setDeleted(false);
        
        permission = new Permission();
        permission.setId(1);
        permission.setName("READ");
    }

    @Test
    void testFindById() {
        //Corregi el metodo que tenia porque no se correspondia con elque estaba en el repo
        when(roleRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(role));
        Role result = roleService.findById(1);
        assertEquals("ADMIN", result.getType());
    }

    @Test
    void testSave() {
        when(roleRepository.findByType("ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(permissionRepository.findById(1)).thenReturn(Optional.of(permission));
        when(rolePermissionRepository.saveAll(any())).thenReturn(List.of(new RolePermission()));
        
        Role result = roleService.save(role, List.of(1));
        assertNotNull(result);
    }

    @Test
    void testFindAll() {
        //Lo mismo
        when(roleRepository.findByIsDeletedFalse()).thenReturn(List.of(role));
        List<Role> result = roleService.findAll();
        assertEquals(1, result.size());
        assertEquals("ADMIN", result.get(0).getType());
    }

    @Test
    void testUpdate() {
        Role updatedData = new Role();
        updatedData.setType("USER");

        when(roleRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role result = roleService.update(1, updatedData);
        assertEquals("USER", result.getType());
    }

    @Test
    void testFindById_NotFound() {
        when(roleRepository.findByIdAndIsDeletedFalse(99)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> roleService.findById(99));
    }

    @Test
    void testDelete_Success() {
        when(roleRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(role));
        doNothing().when(rolePermissionRepository).deleteByRoleId(1);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        
        roleService.delete(1);
        
        assertTrue(role.isDeleted());
        verify(rolePermissionRepository).deleteByRoleId(1);
        verify(roleRepository).save(role);
    }

    @Test
    void testDelete_RoleNotFound() {
        when(roleRepository.findByIdAndIsDeletedFalse(99)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> roleService.delete(99));
        verify(rolePermissionRepository, never()).deleteByRoleId(any());
        verify(roleRepository, never()).save(any());
    }

}