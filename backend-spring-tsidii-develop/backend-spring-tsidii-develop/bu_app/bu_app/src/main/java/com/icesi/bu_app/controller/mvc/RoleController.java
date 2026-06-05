package com.icesi.bu_app.controller.mvc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.icesi.bu_app.model.Permission;
import com.icesi.bu_app.model.Role;
import com.icesi.bu_app.model.RolePermission;
import com.icesi.bu_app.service.IPermissionService;
import com.icesi.bu_app.service.IRolePermissionService;
import com.icesi.bu_app.service.IRoleService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mvc/roles")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class RoleController {
    private final IRoleService roleService;
    private final IPermissionService permissionService;
    private final IRolePermissionService rolePermissionService;

    @PreAuthorize("hasAuthority('READ_ROLE')")
    @GetMapping
    public String getRoles(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<Role> rolesPage = roleService.findAll(page, size);

        model.addAttribute("roles", rolesPage.getContent());
        model.addAttribute("currentPage", rolesPage.getNumber());
        model.addAttribute("totalPages", rolesPage.getTotalPages());
        model.addAttribute("size", rolesPage.getSize());
        return "roles/list";
    }

    @PreAuthorize("hasAuthority('CREATE_ROLE')")
    @GetMapping("/add")
    public String addForm(Model model) {
        Role role = new Role();
        model.addAttribute("role", role);
        model.addAttribute("permissions", permissionService.findAll());
        model.addAttribute("selectedPermissionIds", List.of());
        return "roles/add";
    }

    @PreAuthorize("hasAuthority('DELETE_ROLE')")
    @GetMapping("/delete")
    public String deleteRole(@RequestParam Integer id) {
        roleService.delete(id);
        return "redirect:/roles";
    }

    @PreAuthorize("hasAuthority('READ_ROLE')")
    @GetMapping("/edit")
    public String editForm(@RequestParam Integer id, Model model) {
        Role role = roleService.findById(id);
        List<Integer> selectedPermissionIds = rolePermissionService.getPermissionsByRole(id).stream()
                .map(RolePermission::getPermission)
                .map(Permission::getId)
                .toList();

        model.addAttribute("role", role);
        model.addAttribute("permissions", permissionService.findAll());
        model.addAttribute("selectedPermissionIds", selectedPermissionIds);
        return "roles/edit";
    }

    @PreAuthorize("hasAuthority('ASSIGN_PERMISSION_ROLE')")
    @PostMapping("/edit")
    public String editRole(@ModelAttribute Role role,
            @RequestParam(required = false) List<Integer> permissionIds,
            Model model) {
        try {
            roleService.update(role.getId(), role);
            synchronizePermissions(role.getId(), permissionIds);
            return "redirect:/roles";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("role", role);
            model.addAttribute("permissions", permissionService.findAll());
            model.addAttribute("selectedPermissionIds", permissionIds == null ? List.of() : permissionIds);
            return "roles/edit";
        }
    }

    @PreAuthorize("hasAuthority('CREATE_ROLE')")
    @PostMapping("/add")
    public String addRole(@ModelAttribute Role role,
            @RequestParam(required = false) List<Integer> permissionIds,
            Model model) {
        try {
            roleService.save(role, permissionIds);
            return "redirect:/roles";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("role", role);
            model.addAttribute("permissions", permissionService.findAll());
            model.addAttribute("selectedPermissionIds", permissionIds == null ? List.of() : permissionIds);
            return "roles/add";
        }
    }

    private void synchronizePermissions(Integer roleId, List<Integer> permissionIds) {
        List<RolePermission> currentRelations = rolePermissionService.getPermissionsByRole(roleId);
        Set<Integer> currentPermissionIds = currentRelations.stream()
                .map(RolePermission::getPermission)
                .map(Permission::getId)
                .collect(Collectors.toSet());

        Set<Integer> requestedPermissionIds = permissionIds == null
                ? Set.of()
                : new HashSet<>(permissionIds);

        List<Integer> toRemove = currentPermissionIds.stream()
                .filter(id -> !requestedPermissionIds.contains(id))
                .toList();

        List<Integer> toAdd = requestedPermissionIds.stream()
                .filter(id -> !currentPermissionIds.contains(id))
                .toList();

        toRemove.forEach(permissionId -> rolePermissionService.removePermissionFromRole(roleId, permissionId));

        if (!toAdd.isEmpty()) {
            rolePermissionService.addPermissionToRole(roleId, toAdd);
        }
    }

}
