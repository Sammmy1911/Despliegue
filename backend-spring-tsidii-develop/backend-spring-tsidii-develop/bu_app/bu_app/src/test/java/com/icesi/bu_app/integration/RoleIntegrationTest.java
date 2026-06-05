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
import com.icesi.bu_app.repository.IPermissionRepository;
import com.icesi.bu_app.service.IRoleService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RoleIntegrationTest {

    @Autowired
    private IRoleService roleService;
    @Autowired
    private IPermissionRepository permissionRepository;

    private Permission testPermission;

    @BeforeEach
    void setUp() {
        // Buscar o crear un permiso de prueba
        testPermission = permissionRepository.findAll().stream()
                .filter(p -> "READ".equals(p.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Permission p = new Permission();
                    p.setName("READ");
                    return permissionRepository.save(p);
                });
    }

    @Test
    void testSaveRole() {
        // Creo el rol tipo para evitar la coflictuacion
        Role newRole = new Role();
        newRole.setType("ADMIN_TEST");
        Role saved = roleService.save(newRole, List.of(testPermission.getId()));
        assertNotNull(saved.getId());
    }
}