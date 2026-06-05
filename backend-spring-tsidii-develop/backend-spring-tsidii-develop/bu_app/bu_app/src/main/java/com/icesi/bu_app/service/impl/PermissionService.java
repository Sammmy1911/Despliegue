package com.icesi.bu_app.service.impl;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icesi.bu_app.exception.ConflictException;
import com.icesi.bu_app.exception.ResourceNotFoundException;
import com.icesi.bu_app.model.Permission;
import com.icesi.bu_app.repository.IPermissionRepository;
import com.icesi.bu_app.repository.IRolePermissionRepository;
import com.icesi.bu_app.security.CustomUserDetails;
import com.icesi.bu_app.service.IExerciseService;
import com.icesi.bu_app.service.IPermissionService;
import com.icesi.bu_app.service.IProgressService;
import com.icesi.bu_app.service.IRoutineExerciseService;
import com.icesi.bu_app.service.IUserService;
import com.icesi.bu_app.service.IRoutineService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Service
@RequiredArgsConstructor
public class PermissionService implements IPermissionService {
    private final IUserService userService;
    private final IPermissionRepository permissionRepository;
    private final IRolePermissionRepository rolePermissionRepository;
    private final IRoutineService routineService;
    private final IProgressService progressService;
    private final IExerciseService exerciseService;
    private final IRoutineExerciseService routineExerciseService;

    @Override
    public boolean isAdmin(Authentication auth) {
        if (auth == null) return false;
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @Override
    public boolean isTrainer(Authentication auth) {
        if (auth == null) return false;
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TRAINER"));
    }

    @Override
    public boolean isTrainee(Authentication auth) {
        if (auth == null) return false;
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TRAINEE"));
    }

    @Override
    public Integer getRequesterCode(Authentication auth) {
        if (auth == null) return null;

        if (auth.getPrincipal() instanceof CustomUserDetails cud) {
            return cud.getUser().getCode();
        }

        String username = auth.getName();
        if (username == null || username.isBlank()) {
            return null;
        }

        try {
            return userService.findByEmail(username).getCode();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isRequester(Authentication auth, Integer userId) {
        Integer requesterCode = getRequesterCode(auth);
        return requesterCode != null && requesterCode.equals(userId);
    }

    @Override
    public boolean canAccessTrainer(Authentication auth, Integer trainerId) {
        if (auth == null || trainerId == null) return false;
        if (isAdmin(auth)) return true;
        return isTrainer(auth) && isRequester(auth, trainerId);
    }

    @Override
    public boolean canViewTrainee(Authentication auth, Integer traineeId) {
        if (auth == null || traineeId == null) return false;
        if (isAdmin(auth)) return true;

        Integer requesterCode = getRequesterCode(auth);
        if (requesterCode == null) return false;

        if (isTrainer(auth)) {
            try {
                com.icesi.bu_app.model.User trainee = userService.findById(traineeId);
                return trainee.getTrainer() != null && requesterCode.equals(trainee.getTrainer().getCode());
            } catch (Exception e) {
                return false;
            }
        }

        return requesterCode.equals(traineeId);
    }

    @Override
    public boolean canViewRoutine(Authentication auth, Integer routineId) {
        if (auth == null || routineId == null) return false;
        if (isAdmin(auth)) return true;

        try {
            com.icesi.bu_app.model.Routine routine = routineService.findById(routineId);

            if (isTrainer(auth)) {
                return routine.getTrainer() != null && isRequester(auth, routine.getTrainer().getCode());
            }

            return routine.getTrainee() != null && isRequester(auth, routine.getTrainee().getCode());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean canModifyRoutine(Authentication auth, Integer routineId) {
        return canViewRoutine(auth, routineId);
    }

    @Override
    public boolean canManageRoutine(Authentication auth, Integer trainerCode, Integer traineeCode) {
        if (auth == null) return false;
        if (isAdmin(auth)) return true;

        if (isTrainer(auth)) {
            if (trainerCode == null || !isRequester(auth, trainerCode)) {
                return false;
            }

            if (traineeCode == null) {
                return true;
            }

            try {
                com.icesi.bu_app.model.User trainee = userService.findById(traineeCode);
                return trainee.getTrainer() != null && trainerCode.equals(trainee.getTrainer().getCode());
            } catch (Exception e) {
                return false;
            }
        }

        if (isTrainee(auth) && isRequester(auth, traineeCode)) {
            try {
                com.icesi.bu_app.model.User trainee = userService.findById(traineeCode);
                if (trainerCode == null) {
                    return trainee.getTrainer() != null;
                }
                return trainee.getTrainer() != null && trainerCode.equals(trainee.getTrainer().getCode());
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }

    @Override
    public boolean canManageProgress(Authentication auth, Integer traineeId) {
        if (auth == null || traineeId == null) return false;
        return isAdmin(auth) || canViewTrainee(auth, traineeId);
    }

    @Override
    public boolean canModifyProgress(Authentication auth, Integer progressId) {
        if (auth == null || progressId == null) return false;
        if (isAdmin(auth)) return true;

        try {
            com.icesi.bu_app.model.Progress progress = progressService.findById(progressId);
            return progress.getTrainee() != null && canViewTrainee(auth, progress.getTrainee().getCode());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean canViewProgress(Authentication auth, Integer progressId) {
        if (auth == null || progressId == null) return false;
        if (isAdmin(auth)) return true;

        try {
            com.icesi.bu_app.model.Progress progress = progressService.findById(progressId);
            return progress.getTrainee() != null && canViewTrainee(auth, progress.getTrainee().getCode());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean canManageExercise(Authentication auth, Integer ownerCode, Boolean custom) {
        if (auth == null) return false;

        boolean isCustom = Boolean.TRUE.equals(custom);

        if (isAdmin(auth)) {
            return true;
        }

        if (!isCustom) {
            return isTrainer(auth);
        }

        if (ownerCode == null) {
            return false;
        }

        if (isTrainee(auth) && isRequester(auth, ownerCode)) {
            return true;
        }

        if (isTrainer(auth)) {
            if (isRequester(auth, ownerCode)) {
                return true;
            }

            try {
                com.icesi.bu_app.model.User owner = userService.findById(ownerCode);
                return owner.getTrainer() != null && isRequester(auth, owner.getTrainer().getCode());
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }

    @Override
    public boolean canModifyExercise(Authentication auth, Integer exerciseId) {
        if (auth == null || exerciseId == null) return false;
        if (isAdmin(auth)) return true;

        try {
            com.icesi.bu_app.model.Exercise exercise = exerciseService.findById(exerciseId);

            if (!exercise.isCustom()) {
                return isTrainer(auth);
            }

            if (exercise.getOwner() == null) {
                return false;
            }

            if (isRequester(auth, exercise.getOwner().getCode())) {
                return true;
            }

            if (isTrainer(auth)) {
                return exercise.getOwner().getTrainer() != null
                        && isRequester(auth, exercise.getOwner().getTrainer().getCode());
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean canManageRoutineExercise(Authentication auth, Integer routineId) {
        return canModifyRoutine(auth, routineId);
    }

    @Override
    public boolean canModifyRoutineExercise(Authentication auth, Integer routineExerciseId) {
        if (auth == null || routineExerciseId == null) return false;
        if (isAdmin(auth)) return true;

        try {
            com.icesi.bu_app.model.RoutineExercise routineExercise = routineExerciseService.findById(routineExerciseId);
            return routineExercise.getRoutine() != null && canModifyRoutine(auth, routineExercise.getRoutine().getId());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    @Override
    public Page<Permission> findAll(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));

        PageRequest pageable = PageRequest.of(safePage, safeSize);
        return permissionRepository.findAll(pageable);
    }

    @Override
    public Permission findById(Integer id) {
        return permissionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
    }

    @Override
    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public Permission update(Integer id, Permission permission) {
        Permission foundPermission = findById(id);

        foundPermission.setName(permission.getName());

        return permissionRepository.save(foundPermission);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        findById(id);
        rolePermissionRepository.deleteByPermissionId(id);

        try {
            permissionRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Cannot delete permission because it is still referenced by another entity");
        }
    }

}
