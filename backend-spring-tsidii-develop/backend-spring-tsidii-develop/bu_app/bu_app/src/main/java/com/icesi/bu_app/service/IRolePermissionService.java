package com.icesi.bu_app.service;

import java.util.List;

import com.icesi.bu_app.model.RolePermission;

public interface IRolePermissionService {
    List<RolePermission> addPermissionToRole(Integer roleId, List<Integer> permissionIds);

    void removePermissionFromRole(Integer roleId, Integer permissionId);

    List<RolePermission> getPermissionsByRole(Integer roleId);
}
