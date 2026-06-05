package com.icesi.bu_app.integration;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.icesi.bu_app.controller.mvc.UserController;
import com.icesi.bu_app.model.Role;
import com.icesi.bu_app.model.User;
import com.icesi.bu_app.repository.IRoleRepository;
import com.icesi.bu_app.service.IUserService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserControllerIntegrationTest {
    // Crear un Model real para pasar a los métodos del controller
    private Model model = new ExtendedModelMap();
    @Autowired
    private IUserService userService;
    @Autowired
    private UserController userController;

    @Autowired
    private IRoleRepository roleRepository;

    @Test //Tuve un monton de problemas con el test,  tuve que emular como el inicio de seion con el mockuser
    @WithMockUser(roles = "ADMIN") // Esto asegura que el test se 
    // Spring Security ahora sabe que hay un usuario autenticado con rol ADMIN
    // La anotación @PreAuthorize("hasRole('ADMIN')") se cumple, siiiiiiiiiii
    // ejecute con un usuario autenticado con rol ADMIN
    void testAddUser_Success_WithoutTrainer() {
        // Crear rol único para este test
        Role userRole = new Role();
        userRole.setType("TEST_ROLE_" + System.currentTimeMillis());
        userRole = roleRepository.save(userRole);
        
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("newuser_" + System.currentTimeMillis() + "@test.com");
        newUser.setPassword("password123");
        newUser.setBirthDate(Date.valueOf("1995-05-15"));
        newUser.setSex("M");
        newUser.setRole(userRole);

        String result = userController.addUser(newUser, null, model);

        assertEquals("redirect:/users", result);
    }

    @Test // Este test es para verificar que el formulario de 
    // agregar usuario se muestra correctamente, 
    // no tiene nada que ver con la logica de seguridad, 
    // pero lo deje con el mockuser para evitar problemas de seguridad
    @WithMockUser(roles = "ADMIN")
    void testAddForm_ShouldReturnFormView() {
        var result = userController.addForm(model);
        assertNotNull(result);
        assertEquals("users/add", result);
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Si no se pone el mockuser, 
    // el test falla porque el controller tiene seguridad y no hay un usuario autenticado
    void testGetUsers_ShouldReturnPageOfUsers() {
        var result = userController.getUsers(0, 10, model);
        assertNotNull(result);
        assertEquals("users/list", result);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testEditForm_WithValidId_ShouldReturnEditView() {
        // Crear usuario dentro del test para asegurar que existe
        Role rolin = new Role();
        rolin.setType("EDIT_TEST_" + System.currentTimeMillis());
        rolin = roleRepository.save(rolin);
        User u = new User();
        u.setName("Test User");
        u.setEmail("edit_" + System.currentTimeMillis() + "@test.com");
        u.setPassword("pass");
        u.setBirthDate(Date.valueOf("1990-01-01"));
        u.setSex("M");
        u.setRole(rolin);
        u = userService.save(u);
        String result = userController.editForm(u.getCode(), model);
        assertNotNull(result);
        assertEquals("users/edit", result);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser() {
        //Corroborado con el sql y el modelo
        Role rolin = new Role();
        rolin.setType("DELETE_TEST_" + System.currentTimeMillis()); //Milisegundo del sistema para asegurar un rol unico
        rolin = roleRepository.save(rolin);
        User u = new User();
        u.setName("Temp User");
        u.setEmail("temp_" + System.currentTimeMillis() + "@test.com");
        u.setPassword("temp123");
        u.setBirthDate(Date.valueOf("1990-01-01"));
        u.setSex("M");
        u.setRole(rolin);
        User savedUser = userService.save(u);
        String r = userController.deleteUser(savedUser.getCode());
        assertEquals("redirect:/users", r);
        assertThrows(RuntimeException.class, () -> userService.findById(savedUser.getCode()));
    }

@Test
@WithMockUser(roles = "ADMIN")
void testAddUser_WithoutRole_SRecErr() {
    Role userRole = new Role();
    userRole.setType("ROLE_" + System.currentTimeMillis());
    userRole = roleRepository.save(userRole);
    User newUser = new User();
    newUser.setName("usuarino-chin-rol");
    newUser.setEmail("norole_" + System.currentTimeMillis() + "@test.com");
    newUser.setPassword("password123");
    newUser.setBirthDate(Date.valueOf("1995-05-15"));
    newUser.setSex("M");
    newUser.setRole(null); //ewto se lo que importa
    
    String result = userController.addUser(newUser, null, model);
    
    assertEquals("users/add", result); // Debe volver al formulario
}

@Test
@WithMockUser(roles = "ADMIN")
void testAddUser_WithTrainer_Success() {
    // Crear trainer
    Role trainerRole = new Role();
    trainerRole.setType("TRAINER_" + System.currentTimeMillis());
    trainerRole = roleRepository.save(trainerRole);
    
    User trainer = new User();
    trainer.setName("Trainer");
    trainer.setEmail("trainer_" + System.currentTimeMillis() + "@test.com");
    trainer.setPassword("pass");
    trainer.setBirthDate(Date.valueOf("1990-01-01"));
    trainer.setSex("M");
    trainer.setRole(trainerRole);
    trainer = userService.save(trainer);
    
    // Crear trainee con trainer, osea con el code del trainer
    // , para verificar que el método del controller funciona correctamente con el trainer
    Role userRole = new Role();
    userRole.setType("USER_ROLE_" + System.currentTimeMillis());
    userRole = roleRepository.save(userRole);
    User newUser = new User();
    newUser.setName("Trainee");
    newUser.setEmail("trainee_" + System.currentTimeMillis() + "@test.com");
    newUser.setPassword("password123");
    newUser.setBirthDate(Date.valueOf("1995-05-15"));
    newUser.setSex("F");
    newUser.setRole(userRole);
    
    String result = userController.addUser(newUser, trainer.getCode().toString(), model);
    
    assertEquals("redirect:/users", result);
}

@Test
@WithMockUser(roles = "ADMIN")
void testEditUser_Success() {
    // Crear usuario
    Role role = new Role();
    role.setType("EDIT_USER_" + System.currentTimeMillis());
    role = roleRepository.save(role);
    
    User user = new User();
    user.setName("Original Name");
    user.setEmail("original_" + System.currentTimeMillis() + "@test.com");
    user.setPassword("pass");
    user.setBirthDate(Date.valueOf("1990-01-01"));
    user.setSex("M");
    user.setRole(role);
    user = userService.save(user);
    //Cambiar los datos del usuario, 
    // para verificar que el método del controller funciona correctamente con la edición de usuarios
    User updatedUser = new User();
    updatedUser.setCode(user.getCode());
    updatedUser.setName("Updated Name");
    updatedUser.setEmail("updated_" + System.currentTimeMillis() + "@test.com");
    updatedUser.setBirthDate(Date.valueOf("1990-01-01"));
    updatedUser.setSex("F");
    updatedUser.setRole(role);
    
    String result = userController.editUser(updatedUser, null, model);
    
    assertEquals("redirect:/users", result);
}


}