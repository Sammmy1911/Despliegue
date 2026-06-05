package com.icesi.bu_app.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.icesi.bu_app.model.User;

public interface IUserService {
    Page<User> findAll(int page, int size);

    List<User> findTrainers();

    User findById(Integer id);

    User save(User user);

    User update(Integer id, User user);

    void remove(Integer id);

    User findByEmail(String email);
}