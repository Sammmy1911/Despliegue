package com.icesi.bu_app.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.icesi.bu_app.model.Alert;

public interface IAlertService {
    Alert findById(Integer id);

    List<Alert> findAll();

    Page<Alert> findAll(int page, int size);

    Alert save(Alert alert);

    Alert update(Integer id, Alert alert);

    void delete(Integer id);
}