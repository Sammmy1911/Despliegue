package com.icesi.bu_app.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.icesi.bu_app.controller.mvc.PermissionController;
import com.icesi.bu_app.model.Permission;
import com.icesi.bu_app.repository.IPermissionRepository;
import com.icesi.bu_app.repository.IProgressRepository;
import com.icesi.bu_app.repository.IRolePermissionRepository;
import com.icesi.bu_app.repository.IRoleRepository;
import com.icesi.bu_app.repository.IRoutineRepository;
import com.icesi.bu_app.repository.IUserRepository;
import com.icesi.bu_app.service.IPermissionService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@WithMockUser(roles = "ADMIN")
public class PermissionControllerIntegrationTest {
    @Autowired
    private PermissionController permissionController;
    @Autowired
    private IPermissionService permissionService;
    @Autowired
    private IPermissionRepository permissionRepository;
    @Autowired
    private IRolePermissionRepository rolePermissionRepository;
    @Autowired
    private IRoleRepository roleRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IProgressRepository progressRepository;
    @Autowired
    private IRoutineRepository routineRepository;
    //La manera de  pasar datos desde el controller a la vista (en pocas palabras Thymeleaf).
    private Model model = new ExtendedModelMap();

    @BeforeEach
    //Limpiar las tablas relacionadas para evitar conflictos entre tests
    void setUp() {
        progressRepository.deleteAll();   
        routineRepository.deleteAll();    
        rolePermissionRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        permissionRepository.deleteAll();
    }

    @Test
    void testAddForm() {
        String result = permissionController.addForm(model);
        assertNotNull(result);
        assertEquals("permissions/add", result);
    }

    @Test
    void testAddPermission() {
        Permission newPermission = new Permission();
        newPermission.setName("PER" + System.currentTimeMillis());

        String result = permissionController.addPermission(newPermission, model);

        assertEquals("redirect:/permissions", result);
    }

    @Test
    void testAddPermission_WithDuplicateName() {
        String uniqueName = "DUP_PERM_" + System.currentTimeMillis();
        
        Permission existingPermission = new Permission();
        existingPermission.setName(uniqueName);
        permissionService.save(existingPermission);

        Permission duplicatePermission = new Permission();
        duplicatePermission.setName(uniqueName);

        String r = permissionController.addPermission(duplicatePermission, model);

        assertEquals("permissions/add", r);
    }

    @Test
    void testAddPermission_WithNullName() {
        Permission flus = new Permission();
        flus.setName(null);
        String r = permissionController.addPermission(flus, model);
        assertEquals("permissions/add", r);
    }
 

    @Test
    void testEditForm_WithValidId() {
        Permission tp = new Permission();
        tp.setName("EDIT_FORM_PERM_" + System.currentTimeMillis());
        tp = permissionService.save(tp);
        String result = permissionController.editForm(tp.getId(), model);
        assertNotNull(result);
        assertEquals("permissions/edit", result);
    }

    @Test
    void testEditForm_WithInvalidId() {
        assertThrows(RuntimeException.class, () -> {
            permissionController.editForm(99999, model);
        });
    }

    @Test
    void testEditPermission_theGoodOne() {
        Permission tp = new Permission();
        tp.setName("EDIT_PERM_" + System.currentTimeMillis());
        tp = permissionService.save(tp);
        Permission newp = new Permission();
        newp.setId(tp.getId());
        newp.setName("UPDATED_PERM_" + System.currentTimeMillis());
        String result = permissionController.editPermission(newp, model);
        assertEquals("redirect:/permissions", result);
    }

    @Test
    void testEditPermission_WithInvalidIdr() {
        Permission up = new Permission();
        up.setId(99999);
        up.setName("NONEXISTENT");
        String result = permissionController.editPermission(up, model);
        assertEquals("permissions/edit", result);
    }
    // Aqui se podria agregar un test para verificar que el
    //  nombre unico se mantiene al editar, pero por ahora con esto es suficiente
    @Test
    void testDeletePermission_WithInvalidId() {
        assertThrows(RuntimeException.class, () -> {
            permissionController.deletePermission(99999);
        });
    }
}