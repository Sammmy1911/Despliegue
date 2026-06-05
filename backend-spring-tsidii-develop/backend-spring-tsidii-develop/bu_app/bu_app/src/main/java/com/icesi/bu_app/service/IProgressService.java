package com.icesi.bu_app.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.icesi.bu_app.controller.rest.dto.ProgressAggregationResponse;
import com.icesi.bu_app.model.Progress;
import java.sql.Timestamp;

public interface IProgressService {
    Progress findById(Integer id);

    List<Progress> findAll();

    Page<Progress> findAll(int page, int size);

    Progress save(Progress progress);

    Progress update(Integer id, Progress progress);

    void delete(Integer id);

    List<Progress> findByPerformedAtBetween(Timestamp from, Timestamp to);

    List<Progress> findByTraineeAndPerformedAtBetween(Integer traineeCode, Timestamp from, Timestamp to);

    List<ProgressAggregationResponse> aggregate(String period, Timestamp from, Timestamp to, Integer traineeId);
}