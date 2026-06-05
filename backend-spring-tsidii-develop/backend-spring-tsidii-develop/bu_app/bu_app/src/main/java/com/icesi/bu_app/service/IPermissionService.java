package com.icesi.bu_app.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import com.icesi.bu_app.model.Permission;

public interface IPermissionService {
    boolean isAdmin(Authentication auth);

    boolean isTrainer(Authentication auth);

    boolean isTrainee(Authentication auth);

    Integer getRequesterCode(Authentication auth);

    boolean isRequester(Authentication auth, Integer userId);

    boolean canAccessTrainer(Authentication auth, Integer trainerId);

    boolean canViewTrainee(Authentication auth, Integer traineeId);

    boolean canViewRoutine(Authentication auth, Integer routineId);

    boolean canModifyRoutine(Authentication auth, Integer routineId);

    boolean canManageRoutine(Authentication auth, Integer trainerCode, Integer traineeCode);

    boolean canManageProgress(Authentication auth, Integer traineeId);

    boolean canModifyProgress(Authentication auth, Integer progressId);

    boolean canViewProgress(Authentication auth, Integer progressId);

    boolean canManageExercise(Authentication auth, Integer ownerCode, Boolean custom);

    boolean canModifyExercise(Authentication auth, Integer exerciseId);

    boolean canManageRoutineExercise(Authentication auth, Integer routineId);

    boolean canModifyRoutineExercise(Authentication auth, Integer routineExerciseId);

    Permission findById(Integer id);

    List<Permission> findAll();

    Page<Permission> findAll(int page, int size);

    Permission save(Permission permission);

    Permission update(Integer id, Permission permission);

    void delete(Integer id);
}
