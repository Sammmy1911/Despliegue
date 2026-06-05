package com.icesi.bu_app.controller.mvc;

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
import com.icesi.bu_app.service.IPermissionService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mvc/permissions")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class PermissionController {
	private final IPermissionService permissionService;

	@GetMapping
	public String getPermissions(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model) {
		Page<Permission> permissionsPage = permissionService.findAll(page, size);

		model.addAttribute("permissions", permissionsPage.getContent());
		model.addAttribute("currentPage", permissionsPage.getNumber());
		model.addAttribute("totalPages", permissionsPage.getTotalPages());
		model.addAttribute("size", permissionsPage.getSize());
		return "permissions/list";
	}

	@GetMapping("/add")
	public String addForm(Model model) {
		model.addAttribute("permission", new Permission());
		return "permissions/add";
	}

	@PostMapping("/add")
	public String addPermission(@ModelAttribute Permission permission, Model model) {
		try {
			permissionService.save(permission);
			return "redirect:/permissions";
		} catch (RuntimeException e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("permission", permission);
			return "permissions/add";
		}
	}

	@GetMapping("/edit")
	public String editForm(@RequestParam Integer id, Model model) {
		model.addAttribute("permission", permissionService.findById(id));
		return "permissions/edit";
	}

	@PostMapping("/edit")
	public String editPermission(@ModelAttribute Permission permission, Model model) {
		try {
			permissionService.update(permission.getId(), permission);
			return "redirect:/permissions";
		} catch (RuntimeException e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("permission", permission);
			return "permissions/edit";
		}
	}

	@GetMapping("/delete")
	public String deletePermission(@RequestParam Integer id) {
		permissionService.delete(id);
		return "redirect:/permissions";
	}
}
