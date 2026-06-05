package com.icesi.bu_app.controller.mvc;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.icesi.bu_app.model.Role;
import com.icesi.bu_app.model.User;
import com.icesi.bu_app.service.IRoleService;
import com.icesi.bu_app.service.IUserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mvc/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final IRoleService roleService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String getUsers(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<User> usersPage = userService.findAll(page, size);

        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("currentPage", usersPage.getNumber());
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("size", usersPage.getSize());
        return "users/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add")
    public String addForm(Model model) {
        User user = new User();
        List<Role> roles = roleService.findAll();
        List<User> trainers = userService.findTrainers();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        model.addAttribute("trainers", trainers);
        return "users/add";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete")
    public String deleteUser(@RequestParam Integer id) {
        userService.remove(id);
        return "redirect:/users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit")
    public String editForm(@RequestParam Integer id, Model model) {
        User user = userService.findById(id);
        List<Role> roles = roleService.findAll();
        List<User> trainers = userService.findTrainers();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        model.addAttribute("trainers", trainers);
        return "users/edit";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit")
    public String editUser(@ModelAttribute User user,
            @RequestParam(required = false) String trainerId,
            Model model) {
        try {
            user.setTrainer(resolveTrainer(trainerId));
            userService.update(user.getCode(), user);
            return "redirect:/users";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", roleService.findAll());
            model.addAttribute("trainers", userService.findTrainers());
            model.addAttribute("user", user);
            return "users/edit";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public String addUser(@ModelAttribute User user,
            @RequestParam(required = false) String trainerId,
            Model model) {
        try {
            user.setTrainer(resolveTrainer(trainerId));
            userService.save(user);
            return "redirect:/users";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", roleService.findAll());
            model.addAttribute("trainers", userService.findTrainers());
            model.addAttribute("user", user);
            return "users/add";
        }
    }

    private User resolveTrainer(String trainerId) {
        if (trainerId == null || trainerId.isBlank()) {
            return null;
        }

        return userService.findById(Integer.valueOf(trainerId));
    }
}
