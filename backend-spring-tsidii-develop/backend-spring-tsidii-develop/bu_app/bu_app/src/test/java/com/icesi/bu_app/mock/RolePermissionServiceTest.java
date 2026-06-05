package com.icesi.bu_app.mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.icesi.bu_app.model.*;
import com.icesi.bu_app.repository.*;
import com.icesi.bu_app.service.impl.RolePermissionService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class RolePermissionServiceTest {

    private Permission permission;
    private RolePermission rolePermission;
    private Role role;

    @Mock
    private IRolePermissionRepository rolePermissionRepository;
    @Mock
    private IRoleRepository roleRepository;
    @Mock
    private IPermissionRepository permissionRepository;

    @InjectMocks
    private RolePermissionService service;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1);
        permission = new Permission();
        permission.setId(1);
        rolePermission = new RolePermission();
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
    }

    @Test
    // Este verifica que al agregar un permiso a un rol, se guarda correctamente la
    // relación
    void testAddPermissionToRole_Success() {
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(1)).thenReturn(Optional.of(permission));
        when(rolePermissionRepository.saveAll(anyList())).thenReturn(List.of(rolePermission));

        List<RolePermission> result = service.addPermissionToRole(1, List.of(1));
        assertEquals(1, result.size());
    }

    @Test
    void testAddPermissionToRole_RoleNotFound() {
        when(roleRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.addPermissionToRole(99, List.of(1)));
    }

    @Test
    void testRemovePermissionFromRole_Success() {
        when(rolePermissionRepository.findByRoleId(1)).thenReturn(List.of(rolePermission));
        service.removePermissionFromRole(1, 1);
        verify(rolePermissionRepository).delete(rolePermission);
    }

    @Test
    void testRemovePermissionFromRole_NotFound() {
        // no hay relación con ese permiso
        Permission otherPerm = new Permission();
        otherPerm.setId(2);
        RolePermission other = new RolePermission();
        other.setPermission(otherPerm);
        when(rolePermissionRepository.findByRoleId(1)).thenReturn(List.of(other));

        service.removePermissionFromRole(1, 1);
        verify(rolePermissionRepository, never()).delete(any());
    }

    @Test
    void testGetPermissionsByRole() {
        when(rolePermissionRepository.findByRoleId(1)).thenReturn(List.of(rolePermission));
        List<RolePermission> result = service.getPermissionsByRole(1);
        assertEquals(1, result.size());
    }

}