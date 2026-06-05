package com.icesi.bu_app.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.icesi.bu_app.model.Recommendation;

public interface IRecommendationService {
    Recommendation findById(Integer id);

    List<Recommendation> findAll();

    Page<Recommendation> findAll(int page, int size);

    Recommendation save(Recommendation recommendation);

    Recommendation update(Integer id, Recommendation recommendation);

    void delete(Integer id);
}