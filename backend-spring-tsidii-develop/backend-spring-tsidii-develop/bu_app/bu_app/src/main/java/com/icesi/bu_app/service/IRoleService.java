package com.icesi.bu_app.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.icesi.bu_app.model.Role;

public interface IRoleService {
    Role findById(Integer id);
    
    List<Role> findAll();

    Page<Role> findAll(int page, int size);

    Role save(Role role, List<Integer> permissionIds);

    Role update(Integer id, Role role);

    void delete(Integer id);
}
