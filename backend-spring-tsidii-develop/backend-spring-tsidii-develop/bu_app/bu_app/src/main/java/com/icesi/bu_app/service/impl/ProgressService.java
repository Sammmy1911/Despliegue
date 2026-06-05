package com.icesi.bu_app.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.WeekFields;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icesi.bu_app.model.Progress;
import com.icesi.bu_app.model.ProgressType;
import com.icesi.bu_app.model.Routine;
import com.icesi.bu_app.model.StressLevel;
import com.icesi.bu_app.model.User;
import com.icesi.bu_app.repository.IProgressRepository;
import com.icesi.bu_app.repository.IProgressTypeRepository;
import com.icesi.bu_app.repository.IRoutineRepository;
import com.icesi.bu_app.repository.IStressLevelRepository;
import com.icesi.bu_app.repository.IUserRepository;
import com.icesi.bu_app.service.IProgressService;
import com.icesi.bu_app.exception.ResourceNotFoundException;
import com.icesi.bu_app.controller.rest.dto.ProgressAggregationResponse;
import com.icesi.bu_app.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProgressService implements IProgressService {

    private final IProgressRepository progressRepository;
    private final IStressLevelRepository stressLevelRepository;
    private final IProgressTypeRepository progressTypeRepository;
    private final IUserRepository userRepository;
    private final IRoutineRepository routineRepository;

    @Override
    public List<Progress> findAll() {
        return progressRepository.findAll();
    }

    @Override
    public Page<Progress> findAll(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));
        return progressRepository.findAll(PageRequest.of(safePage, safeSize));
    }

    @Override
    public Progress findById(Integer id) {
        return progressRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Progress not found"));
    }

    @Override
    @Transactional
    public Progress save(Progress progress) {
        if (progress.getPerformedAt() == null) {
            progress.setPerformedAt(new Timestamp(System.currentTimeMillis()));
        }
        progress.setStressLevel(resolveRequiredStressLevel(progress.getStressLevel()));
        progress.setProgressType(resolveRequiredProgressType(progress.getProgressType()));
        progress.setTrainee(resolveRequiredUser(progress.getTrainee()));
        progress.setRoutine(resolveRequiredRoutine(progress.getRoutine()));
        return progressRepository.save(progress);
    }

    @Override
    @Transactional
    public Progress update(Integer id, Progress progress) {
        Progress foundProgress = findById(id);
        foundProgress.setRepetitions(progress.getRepetitions());
        foundProgress.setTime(progress.getTime());
        if (progress.getPerformedAt() != null) {
            foundProgress.setPerformedAt(progress.getPerformedAt());
        }
        foundProgress.setStressLevel(resolveRequiredStressLevel(progress.getStressLevel()));
        foundProgress.setProgressType(resolveRequiredProgressType(progress.getProgressType()));
        foundProgress.setTrainee(resolveRequiredUser(progress.getTrainee()));
        foundProgress.setRoutine(resolveRequiredRoutine(progress.getRoutine()));
        return progressRepository.save(foundProgress);
    }

    @Override
    public List<Progress> findByPerformedAtBetween(Timestamp from, Timestamp to) {
        return progressRepository.findByPerformedAtBetween(from, to);
    }

    @Override
    public List<Progress> findByTraineeAndPerformedAtBetween(Integer traineeCode, Timestamp from, Timestamp to) {
        return progressRepository.findByTraineeCodeAndPerformedAtBetween(traineeCode, from, to);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        findById(id);
        progressRepository.deleteById(id);
    }

    @Override
    public List<ProgressAggregationResponse> aggregate(String period, Timestamp from, Timestamp to, Integer traineeId) {
        List<Progress> progresses;
        if (traineeId != null) {
            progresses = findByTraineeAndPerformedAtBetween(traineeId, from, to);
        } else {
            progresses = findByPerformedAtBetween(from, to);
        }

        List<ProgressAggregationResponse> agg;
        if ("weekly".equalsIgnoreCase(period)) {
            WeekFields wf = WeekFields.ISO;
            Map<String, List<Progress>> grouped = progresses.stream()
                    .collect(Collectors.groupingBy(p -> {
                        LocalDate d = p.getPerformedAt().toLocalDateTime().toLocalDate();
                        int week = d.get(wf.weekOfWeekBasedYear());
                        int year = d.get(wf.weekBasedYear());
                        return String.format("%d-W%02d", year, week);
                    }));

            agg = grouped.entrySet().stream().map(e -> new ProgressAggregationResponse(
                    e.getKey(), e.getValue().stream().mapToInt(Progress::getRepetitions).sum(),
                    e.getValue().size())).collect(Collectors.toList());
        } else {
            Map<LocalDate, List<Progress>> grouped = progresses.stream()
                    .collect(Collectors.groupingBy(p -> p.getPerformedAt().toLocalDateTime().toLocalDate()));

            agg = grouped.entrySet().stream().map(e -> new ProgressAggregationResponse(
                    e.getKey().toString(), e.getValue().stream().mapToInt(Progress::getRepetitions).sum(),
                    e.getValue().size())).collect(Collectors.toList());
        }

        return agg;
    }

    private StressLevel resolveRequiredStressLevel(StressLevel stressLevel) {
        if (stressLevel == null || stressLevel.getId() == null) {
            throw new BadRequestException("Progress must have a stress level");
        }
        return stressLevelRepository.findById(stressLevel.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Stress level not found"));
    }

    private ProgressType resolveRequiredProgressType(ProgressType progressType) {
        if (progressType == null || progressType.getId() == null) {
            throw new BadRequestException("Progress must have a progress type");
        }
        return progressTypeRepository.findById(progressType.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Progress type not found"));
    }

    private User resolveRequiredUser(User user) {
        if (user == null || user.getCode() == null) {
            throw new BadRequestException("Progress must have a trainee");
        }
        return userRepository.findById(user.getCode())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Routine resolveRequiredRoutine(Routine routine) {
        if (routine == null || routine.getId() == null) {
            throw new BadRequestException("Progress must have a routine");
        }
        return routineRepository.findById(routine.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Routine not found"));
    }
}