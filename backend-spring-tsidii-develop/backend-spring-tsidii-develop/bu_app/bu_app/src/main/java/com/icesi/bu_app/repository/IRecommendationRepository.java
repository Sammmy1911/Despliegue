package com.icesi.bu_app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.icesi.bu_app.model.Recommendation;

@Repository
public interface IRecommendationRepository extends JpaRepository<Recommendation, Integer> {
    // Buscar recomendaciones por código del trainer
    List<Recommendation> findByTrainerCode(Integer trainerCode);

    List<Recommendation> findByProgressId(Integer progressId);
}
