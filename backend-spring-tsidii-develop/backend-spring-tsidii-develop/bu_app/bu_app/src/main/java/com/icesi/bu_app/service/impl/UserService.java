package com.icesi.bu_app.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.icesi.bu_app.model.Role;
import com.icesi.bu_app.model.User;
import com.icesi.bu_app.repository.IUserRepository;
import com.icesi.bu_app.service.IRoleService;
import com.icesi.bu_app.service.IUserService;
import com.icesi.bu_app.exception.ResourceNotFoundException;
import com.icesi.bu_app.exception.BadRequestException;
import com.icesi.bu_app.exception.ConflictException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final IRoleService roleService;

    @Override
    public Page<User> findAll(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));

        PageRequest pageable = PageRequest.of(safePage, safeSize);
        return userRepository.findByIsDeletedFalse(pageable);
    }

    @Override
    public User findById(Integer id) {
        return userRepository.findByCodeAndIsDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User save(User user) {
        if (user.getRole() == null || user.getRole().getId() == null) {
            throw new BadRequestException("User must have a role");
        }

        Role role = roleService.findById(user.getRole().getId());

        User targetUser = userRepository.findByEmail(user.getEmail())
                .map(existingUser -> {
                    if (!existingUser.isDeleted()) {
                        throw new ConflictException("User already exists");
                    }

                    existingUser.setDeleted(false);
                    existingUser.setName(user.getName());
                    existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                    existingUser.setBirthDate(user.getBirthDate());
                    existingUser.setSex(user.getSex());
                    existingUser.setRole(role);
                    existingUser.setTrainer(user.getTrainer());
                    return existingUser;
                })
                .orElseGet(() -> {
                    user.setDeleted(false);
                    user.setRole(role);
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    return user;
                });

        return userRepository.save(targetUser);
    }

    @Override
    public User update(Integer id, User user) {
        User foundUser = findById(id);

        // Business logic validation
        if (user.getRole() == null || user.getRole().getId() == null) {
            throw new BadRequestException("User must have a role");
        }

        Role role = roleService.findById(user.getRole().getId());

        foundUser.setName(user.getName());
        foundUser.setEmail(user.getEmail());
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            foundUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        foundUser.setBirthDate(user.getBirthDate());
        foundUser.setSex(user.getSex());
        foundUser.setRole(role);
        foundUser.setTrainer(user.getTrainer());

        return userRepository.save(foundUser);
    }

    @Override
    public void remove(Integer id) {
        User user = findById(id);
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    public List<User> findTrainers() {
        return userRepository.findByRoleTypeAndIsDeletedFalse("TRAINER");
    }

    @Override
    public User findByEmail(String email){
        return userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

}
