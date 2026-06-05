package com.icesi.bu_app.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.icesi.bu_app.model.*;
import com.icesi.bu_app.repository.IPermissionRepository;
import com.icesi.bu_app.repository.IRolePermissionRepository;
import com.icesi.bu_app.repository.IRoleRepository;
import com.icesi.bu_app.service.IUserService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserIntegrationTest {

    @Autowired
    private IUserService userService;
    
    @Autowired
    private IRoleRepository roleRepository;
    
    @Autowired
    private IPermissionRepository permissionRepository;
    
    @Autowired
    private IRolePermissionRepository rolePermissionRepository;

    @Test
    void testSaveAndFind() {
        // 1. Crear permiso
        Permission perm = new Permission();
        perm.setName("TEST_PERM_" + System.currentTimeMillis());
        perm = permissionRepository.save(perm);
        
        // 2. Crear rol
        Role testRole = new Role();
        testRole.setType("TEST_" + System.currentTimeMillis());
        testRole = roleRepository.save(testRole);
        
        // 3. Crear relación Role-Permission
        RolePermission rp = new RolePermission();
        rp.setRole(testRole);
        rp.setPermission(perm);
        rolePermissionRepository.save(rp);
        
        // 4. Crear usuario
        User user = new User();
        user.setName("palo");
        user.setEmail("palo_" + System.currentTimeMillis() + "@test.com");
        user.setPassword("123");
        user.setBirthDate(Date.valueOf("1990-01-01"));
        user.setSex("M");
        user.setRole(testRole);
        
        User saved = userService.save(user);
        User found = userService.findById(saved.getCode());
        
        assertNotNull(found);
    }
}