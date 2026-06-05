package com.icesi.bu_app.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.icesi.bu_app.model.ProgressType;

public interface IProgressTypeService {
    ProgressType findById(Integer id);

    List<ProgressType> findAll();

    Page<ProgressType> findAll(int page, int size);

    ProgressType save(ProgressType progressType);

    ProgressType update(Integer id, ProgressType progressType);

    void delete(Integer id);
}