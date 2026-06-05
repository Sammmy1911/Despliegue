package com.icesi.bu_app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.icesi.bu_app.model.RolePermission;

@Repository
public interface IRolePermissionRepository extends JpaRepository<RolePermission, Integer> {
    List<RolePermission> findByRoleId(Integer roleId); // Verificar permison a roles

    List<RolePermission> findByPermissionId(Integer permissionId);

    void deleteByRoleId(Integer roleId);

    void deleteByPermissionId(Integer permissionId);

}
