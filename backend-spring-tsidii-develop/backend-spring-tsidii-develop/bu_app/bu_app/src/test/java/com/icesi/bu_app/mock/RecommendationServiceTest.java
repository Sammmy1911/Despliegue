package com.icesi.bu_app.mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.icesi.bu_app.model.Progress;
import com.icesi.bu_app.model.Recommendation;
import com.icesi.bu_app.model.User;
import com.icesi.bu_app.repository.IProgressRepository;
import com.icesi.bu_app.repository.IRecommendationRepository;
import com.icesi.bu_app.repository.IUserRepository;
import com.icesi.bu_app.service.impl.RecommendationService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class RecommendationServiceTest {

    @Mock
    private IRecommendationRepository recommendationRepository;
    @Mock
    private IUserRepository userRepository;
    @Mock
    private IProgressRepository progressRepository;
    @InjectMocks
    private RecommendationService service;

    private Recommendation recommendation;
    private User trainer;
    private Progress progress;

    @BeforeEach
    void setUp() {
        trainer = new User();
        trainer.setCode(1);

        progress = new Progress();
        progress.setId(1);

        recommendation = new Recommendation();
        recommendation.setId(1);
        recommendation.setDescription("Keep going");
        recommendation.setTrainer(trainer);
        recommendation.setProgress(progress);
    }

    @Test
    void testFindAll() {
        when(recommendationRepository.findAll()).thenReturn(List.of(recommendation));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void testFindById() {
        when(recommendationRepository.findById(1)).thenReturn(Optional.of(recommendation));
        assertEquals("Keep going", service.findById(1).getDescription());
    }

    @Test
    void testSave() {
        when(userRepository.findById(1)).thenReturn(Optional.of(trainer));
        when(progressRepository.findById(1)).thenReturn(Optional.of(progress));
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);

        assertNotNull(service.save(recommendation));
    }

    @Test
    void testUpdate() {
        when(recommendationRepository.findById(1)).thenReturn(Optional.of(recommendation));
        when(userRepository.findById(1)).thenReturn(Optional.of(trainer));
        when(progressRepository.findById(1)).thenReturn(Optional.of(progress));
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);

        Recommendation updated = new Recommendation();
        updated.setDescription("Updated");
        updated.setTrainer(trainer);
        updated.setProgress(progress);

        Recommendation result = service.update(1, updated);

        assertNotNull(result);
        verify(recommendationRepository).save(any(Recommendation.class));
    }

    @Test
    void testDelete() {
        when(recommendationRepository.findById(1)).thenReturn(Optional.of(recommendation));
        doNothing().when(recommendationRepository).deleteById(1);

        service.delete(1);

        verify(recommendationRepository).deleteById(1);
    }
}