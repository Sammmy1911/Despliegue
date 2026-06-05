package com.icesi.bu_app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.icesi.bu_app.model.Permission;
import com.icesi.bu_app.model.Role;
import com.icesi.bu_app.model.RolePermission;
import com.icesi.bu_app.repository.IPermissionRepository;
import com.icesi.bu_app.repository.IRolePermissionRepository;
import com.icesi.bu_app.repository.IRoleRepository;
import com.icesi.bu_app.service.IRolePermissionService;
import com.icesi.bu_app.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolePermissionService implements IRolePermissionService {

    private final IRolePermissionRepository rolePermissionRepository;
    private final IRoleRepository roleRepository;
    private final IPermissionRepository permissionRepository;

    @Override
    public List<RolePermission> addPermissionToRole(Integer roleId, List<Integer> permissionIds) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        List<RolePermission> relations = permissionIds.stream().map(permissionId -> {
                Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

            RolePermission rolePermission = new RolePermission();
            rolePermission.setRole(role);
            rolePermission.setPermission(permission);

            return rolePermission;
        }).toList();

        return rolePermissionRepository.saveAll(relations);
    }

    @Override
    public void removePermissionFromRole(Integer roleId, Integer permissionId) {
        List<RolePermission> relations = rolePermissionRepository.findByRoleId(roleId);

        relations.stream().filter(rolePermission -> rolePermission.getPermission().getId().equals(permissionId))
                .findFirst().ifPresent(rp -> rolePermissionRepository.delete(rp));
    }

    @Override
    public List<RolePermission> getPermissionsByRole(Integer roleId) {
        return rolePermissionRepository.findByRoleId(roleId);
    }

}
