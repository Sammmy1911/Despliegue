package com.icesi.bu_app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.icesi.bu_app.model.Progress;
import java.sql.Timestamp;

@Repository
public interface IProgressRepository extends JpaRepository<Progress, Integer> {

    List<Progress> findByTraineeCode(Integer traineeCode);

    List<Progress> findByStressLevelId(Integer stressLevelId);

    List<Progress> findByProgressTypeId(Integer progressTypeId);

    List<Progress> findByTraineeCodeAndStressLevelId(Integer traineeCode, Integer stressLevelId);

    List<Progress> findByTraineeCodeAndProgressTypeId(Integer traineeCode, Integer progressTypeId);

    List<Progress> findByPerformedAtBetween(Timestamp from, Timestamp to);

    List<Progress> findByTraineeCodeAndPerformedAtBetween(Integer traineeCode, Timestamp from,
            Timestamp to);
}
