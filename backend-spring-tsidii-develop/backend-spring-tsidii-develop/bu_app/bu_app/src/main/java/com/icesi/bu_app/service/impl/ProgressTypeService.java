package com.icesi.bu_app.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.icesi.bu_app.model.ProgressType;
import com.icesi.bu_app.repository.IProgressTypeRepository;
import com.icesi.bu_app.service.IProgressTypeService;
import com.icesi.bu_app.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProgressTypeService implements IProgressTypeService {

    private final IProgressTypeRepository progressTypeRepository;

    @Override
    public List<ProgressType> findAll() {
        return progressTypeRepository.findAll();
    }

    @Override
    public Page<ProgressType> findAll(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));
        return progressTypeRepository.findAll(PageRequest.of(safePage, safeSize));
    }

    @Override
    public ProgressType findById(Integer id) {
        return progressTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Progress type not found"));
    }

    @Override
    public ProgressType save(ProgressType progressType) {
        return progressTypeRepository.save(progressType);
    }

    @Override
    public ProgressType update(Integer id, ProgressType progressType) {
        ProgressType foundProgressType = findById(id);
        foundProgressType.setType(progressType.getType());
        return progressTypeRepository.save(foundProgressType);
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        progressTypeRepository.deleteById(id);
    }
}