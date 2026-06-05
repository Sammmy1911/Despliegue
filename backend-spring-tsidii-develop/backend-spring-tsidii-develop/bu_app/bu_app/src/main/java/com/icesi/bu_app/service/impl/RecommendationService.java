package com.icesi.bu_app.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.icesi.bu_app.model.Progress;
import com.icesi.bu_app.model.Recommendation;
import com.icesi.bu_app.model.User;
import com.icesi.bu_app.repository.IProgressRepository;
import com.icesi.bu_app.repository.IRecommendationRepository;
import com.icesi.bu_app.repository.IUserRepository;
import com.icesi.bu_app.service.IRecommendationService;
import com.icesi.bu_app.exception.ResourceNotFoundException;
import com.icesi.bu_app.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationService implements IRecommendationService {

    private final IRecommendationRepository recommendationRepository;
    private final IUserRepository userRepository;
    private final IProgressRepository progressRepository;

    @Override
    public List<Recommendation> findAll() {
        return recommendationRepository.findAll();
    }

    @Override
    public Page<Recommendation> findAll(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));
        return recommendationRepository.findAll(PageRequest.of(safePage, safeSize));
    }

    @Override
    public Recommendation findById(Integer id) {
        return recommendationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found"));
    }

    @Override
    public Recommendation save(Recommendation recommendation) {
        recommendation.setTrainer(resolveRequiredUser(recommendation.getTrainer()));
        recommendation.setProgress(resolveProgress(recommendation.getProgress()));
        return recommendationRepository.save(recommendation);
    }

    @Override
    public Recommendation update(Integer id, Recommendation recommendation) {
        Recommendation foundRecommendation = findById(id);
        foundRecommendation.setDescription(recommendation.getDescription());
        foundRecommendation.setTrainer(resolveRequiredUser(recommendation.getTrainer()));
        foundRecommendation.setProgress(resolveProgress(recommendation.getProgress()));
        return recommendationRepository.save(foundRecommendation);
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        recommendationRepository.deleteById(id);
    }

    private User resolveRequiredUser(User user) {
        if (user == null || user.getCode() == null) {
            throw new BadRequestException("Recommendation must have a trainer");
        }
        return userRepository.findById(user.getCode())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Progress resolveProgress(Progress progress) {
        if (progress == null) {
            return null;
        }
        if (progress.getId() == null) {
            throw new ResourceNotFoundException("Progress not found");
        }
        return progressRepository.findById(progress.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Progress not found"));
    }
}