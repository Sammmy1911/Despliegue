package com.icesi.bu_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.icesi.bu_app.model.User;

@Repository
public interface IUserRepository extends JpaRepository<User, Integer> {
    Page<User> findByIsDeletedFalse(Pageable pageable);

    Optional<User> findByCodeAndIsDeletedFalse(Integer code);

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    Optional<User> findByEmail(String email);

    List<User> findByTrainerCodeAndIsDeletedFalse(Integer trainerCode);

    List<User> findByRoleTypeAndIsDeletedFalse(String roleType);

    List<User> findByRoleIdAndIsDeletedFalse(Integer roleId);
}