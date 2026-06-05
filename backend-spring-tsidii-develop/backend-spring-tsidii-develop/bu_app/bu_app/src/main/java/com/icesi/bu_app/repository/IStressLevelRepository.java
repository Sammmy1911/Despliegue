package com.icesi.bu_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.icesi.bu_app.model.StressLevel;

@Repository
public interface IStressLevelRepository extends JpaRepository<StressLevel, Integer> {

}
