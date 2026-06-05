package com.icesi.bu_app.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.icesi.bu_app.model.*;
import com.icesi.bu_app.repository.*;
import com.icesi.bu_app.service.IRolePermissionService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RolePermissionIntegrationTest {

    @Autowired
    private IRolePermissionService rolePermissionService;
    @Autowired
    private IRoleRepository roleRepository;
    @Autowired
    private IPermissionRepository permissionRepository;

    private Role role;
    private Permission permission;

    @BeforeEach
    void setUp() {
        // Buscar o crear rol de prueba
        role = roleRepository.findAll().stream() // Esto es para evitar problemas de unicidad si el test se ejecuta
                                                 // varias veces sin limpiar la base de datos
                .filter(r -> "TEST_ROLE".equals(r.getType()))
                .findFirst()
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setType("TEST_ROLE");
                    return roleRepository.save(r);
                });

        // Buscar o crear permiso de prueba
        permission = permissionRepository.findAll().stream()
                .filter(p -> "TEST_PERMISSION".equals(p.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Permission p = new Permission();
                    p.setName("TEST_PERMISSION");
                    return permissionRepository.save(p);
                });
    }

    @Test
    void testAddPermissionToRole() {
        List<RolePermission> busca = rolePermissionService.addPermissionToRole(role.getId(),
                List.of(permission.getId()));
        assertNotNull(busca);
        assertEquals(1, busca.size());

        RolePermission rolcitopermision = busca.get(0);
        assertEquals(role.getId(), rolcitopermision.getRole().getId());
        assertEquals(permission.getId(), rolcitopermision.getPermission().getId());

        List<RolePermission> found = rolePermissionService.getPermissionsByRole(role.getId());
        assertEquals(1, found.size());
        assertEquals(permission.getId(), found.get(0).getPermission().getId());
    }

    @Test
    void testRemovePermissionFromRole() {
        rolePermissionService.addPermissionToRole(role.getId(), List.of(permission.getId()));
        List<RolePermission> before = rolePermissionService.getPermissionsByRole(role.getId());
        assertEquals(1, before.size());
        rolePermissionService.removePermissionFromRole(role.getId(), permission.getId());
        List<RolePermission> after = rolePermissionService.getPermissionsByRole(role.getId());
        assertTrue(after.isEmpty());
    }
}