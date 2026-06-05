package com.icesi.bu_app.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.icesi.bu_app.controller.mvc.RoleController;
import com.icesi.bu_app.model.Permission;
import com.icesi.bu_app.model.Role;
import com.icesi.bu_app.repository.IPermissionRepository;
import com.icesi.bu_app.service.IRoleService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RoleControllerIntegrationTest {
    //La manera de  pasar datos desde el controller a la vista (en pocas palabras Thymeleaf).
    private Model model = new ExtendedModelMap();
    private Permission testinObj;// Ob
    @Autowired
    private RoleController roleController;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IPermissionRepository permissionRepository;
//Me toco cambiar el enfoque porque el test no funcionaba,
//Lo que pasa es que el permision funciona mediante las autorithies basicamente,
//Como lo estab haciendo con los usuarios, cuando corria los test fallaban casi todos
    @BeforeEach
    void setUp() {
        // Crear un permiso de prueba
        testinObj = permissionRepository.findAll().stream()
                .filter(p -> "READ".equals(p.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Permission p = new Permission();
                    p.setName("READ");
                    return permissionRepository.save(p);
                });
    }
    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "READ_ROLE"})
    void testGetRoles() {
        Role testRole = new Role();
        testRole.setType("TEST_GET_ROLES_" + System.currentTimeMillis());
        testRole = roleService.save(testRole, List.of(testinObj.getId()));

        String result = roleController.getRoles(0, 10, model);

        assertNotNull(result);
        assertEquals("roles/list", result);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "CREATE_ROLE"})
    void testAddForm() {
        String result = roleController.addForm(model);

        assertNotNull(result);
        assertEquals("roles/add", result);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "CREATE_ROLE"})
    void testAddRole_Success() {
        Role newRole = new Role();
        newRole.setType("NEW_ROLE_" + System.currentTimeMillis());

        String result = roleController.addRole(newRole, List.of(testinObj.getId()), model);

        assertEquals("redirect:/roles", result);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "CREATE_ROLE"})
    void testAddRole_WithEmptyPermission() {
        Role newRole = new Role();
        newRole.setType("EMPTY_PERM_ROLE_" + System.currentTimeMillis());

        String result = roleController.addRole(newRole, List.of(), model);

        assertEquals("roles/add", result);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "READ_ROLE"})
    void testEditForm_WithInvalidId_ShouldThrowException() {
        assertThrows(RuntimeException.class, () -> {
            roleController.editForm(99999, model);
        });
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ASSIGN_PERMISSION_ROLE"})
    void testEditRole_WithoutPermissions() {
        Role testRole = new Role();
        testRole.setType("EDIT_ROLE_" + System.currentTimeMillis());
        testRole = roleService.save(testRole, List.of(testinObj.getId()));

        Role updatedRole = new Role();
        updatedRole.setId(testRole.getId());
        updatedRole.setType("UPDATED_ROLE_" + System.currentTimeMillis());

        String result = roleController.editRole(updatedRole, null, model);

        assertEquals("redirect:/roles", result);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ASSIGN_PERMISSION_ROLE"})
    void testEditRole_WithInvalidId() {
        Role updatedRole = new Role();
        updatedRole.setId(99999);
        updatedRole.setType("NONEXISTENT");

        String result = roleController.editRole(updatedRole, List.of(testinObj.getId()), model);

        assertEquals("roles/edit", result);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "DELETE_ROLE"})
    void testDeleteRole_Success() {
        Role tempRole = new Role();
        tempRole.setType("DELETE_ROLE_" + System.currentTimeMillis());
        tempRole = roleService.save(tempRole, List.of(testinObj.getId()));

        String result = roleController.deleteRole(tempRole.getId());

        assertEquals("redirect:/roles", result);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "DELETE_ROLE"})
    void testDeleteRole_WithInvalidId_ShouldThrowException() {
        assertThrows(RuntimeException.class, () -> {
            roleController.deleteRole(99999);
        });
    }
}