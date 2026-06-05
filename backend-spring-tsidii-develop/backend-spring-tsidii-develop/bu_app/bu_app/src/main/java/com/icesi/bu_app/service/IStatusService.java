package com.icesi.bu_app.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.icesi.bu_app.model.Status;

public interface IStatusService {
    Status findById(Integer id);

    List<Status> findAll();

    Page<Status> findAll(int page, int size);

    Status save(Status status);

    Status update(Integer id, Status status);

    void delete(Integer id);
}