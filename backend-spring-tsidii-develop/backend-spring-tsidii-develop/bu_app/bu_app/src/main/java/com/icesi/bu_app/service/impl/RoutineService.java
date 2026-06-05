package com.icesi.bu_app.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.icesi.bu_app.exception.BadRequestException;
import com.icesi.bu_app.exception.ConflictException;
import com.icesi.bu_app.exception.ResourceNotFoundException;
import com.icesi.bu_app.model.Routine;
import com.icesi.bu_app.model.User;
import com.icesi.bu_app.repository.IRoutineRepository;
import com.icesi.bu_app.repository.IUserRepository;
import com.icesi.bu_app.service.IRoutineService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoutineService implements IRoutineService {

    private final IRoutineRepository routineRepository;
    private final IUserRepository userRepository;

    @Override
    public List<Routine> findAll() {
        return routineRepository.findAll();
    }

    @Override
    public Page<Routine> findAll(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));
        return routineRepository.findAll(PageRequest.of(safePage, safeSize));
    }

    @Override
    public Routine findById(Integer id) {
        return routineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Routine not found"));
    }

    @Override
    public Routine save(Routine routine) {
        routine.setTrainee(resolveOptionalUser(routine.getTrainee()));
        routine.setTrainer(resolveTrainer(routine));
        return routineRepository.save(routine);
    }

    @Override
    public Routine update(Integer id, Routine routine) {
        Routine foundRoutine = findById(id);
        foundRoutine.setName(routine.getName());
        foundRoutine.setTrainee(resolveOptionalUser(routine.getTrainee()));
        foundRoutine.setTrainer(resolveTrainer(routine));
        return routineRepository.save(foundRoutine);
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        try {
            routineRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "No se puede eliminar la rutina porque tiene progresos asociados. Elimine primero los progresos relacionados.");
        }
    }

    @Override
    public List<Routine> findByTrainerCode(Integer trainerCode) {
        return routineRepository.findByTrainerCode(trainerCode);
    }

    @Override
    public List<Routine> findByTraineeCode(Integer traineeCode) {
        return routineRepository.findByTraineeCode(traineeCode);
    }

    private User resolveRequiredUser(User user, String role) {
        if (user == null || user.getCode() == null) {
            throw new BadRequestException("Routine must have a " + role);
        }
        return userRepository.findById(user.getCode())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private User resolveOptionalUser(User user) {
        if (user == null || user.getCode() == null) {
            return null;
        }
        return userRepository.findById(user.getCode())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private User resolveTrainer(Routine routine) {
        if (routine.getTrainer() != null && routine.getTrainer().getCode() != null) {
            return resolveRequiredUser(routine.getTrainer(), "trainer");
        }

        User trainee = resolveRequiredUser(routine.getTrainee(), "trainee");
        if (trainee.getTrainer() == null) {
            throw new BadRequestException("Routine must have a trainer or the trainee must have one assigned");
        }

        return resolveRequiredUser(trainee.getTrainer(), "trainer");
    }
}
