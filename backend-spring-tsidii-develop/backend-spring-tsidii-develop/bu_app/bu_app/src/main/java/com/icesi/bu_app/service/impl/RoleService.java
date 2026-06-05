package com.icesi.bu_app.service.impl;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icesi.bu_app.model.Permission;
import com.icesi.bu_app.model.Role;
import com.icesi.bu_app.model.RolePermission;
import com.icesi.bu_app.repository.IPermissionRepository;
import com.icesi.bu_app.repository.IRolePermissionRepository;
import com.icesi.bu_app.repository.IRoleRepository;
import com.icesi.bu_app.service.IRoleService;

import lombok.RequiredArgsConstructor;
import com.icesi.bu_app.exception.ResourceNotFoundException;
import com.icesi.bu_app.exception.BadRequestException;
import com.icesi.bu_app.exception.ConflictException;
import org.springframework.data.domain.Page;


@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final IRoleRepository roleRepository;
    private final IRolePermissionRepository rolePermissionRepository;
    private final IPermissionRepository permissionRepository;

    @Override
    public Page<Role> findAll(int page, int size) {
                int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));

        PageRequest pageable = PageRequest.of(safePage, safeSize);
        return roleRepository.findByIsDeletedFalse(pageable);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findByIsDeletedFalse();
    }

    @Override
    public Role findById(Integer id) {
        return roleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }

    @Override
    @Transactional
    public Role save(Role role, List<Integer> permissionIds) {

        if (permissionIds == null || permissionIds.isEmpty()) {
            throw new BadRequestException("Role must have at least one permission");
        }

        Role targetRole = roleRepository.findByType(role.getType())
                .map(existingRole -> {
                    if (!existingRole.isDeleted()) {
                        throw new ConflictException("Role already exists");
                    }

                    existingRole.setDeleted(false);
                    return roleRepository.save(existingRole);
                })
                .orElseGet(() -> {
                    role.setDeleted(false);
                    return roleRepository.save(role);
                });

        rolePermissionRepository.deleteByRoleId(targetRole.getId());

        List<RolePermission> relations = permissionIds.stream().map(permissionId -> {
                    Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

            RolePermission rp = new RolePermission();
            rp.setRole(targetRole);
            rp.setPermission(permission);

            return rp;

        }).toList();

        rolePermissionRepository.saveAll(relations);

        return targetRole;
    }

    @Override
    public Role update(Integer id, Role role) {
        Role foundRole = findById(id);
        foundRole.setType(role.getType());

        return roleRepository.save(foundRole);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Role role = findById(id);
        rolePermissionRepository.deleteByRoleId(id);
        role.setDeleted(true);
        roleRepository.save(role);
    }
}
