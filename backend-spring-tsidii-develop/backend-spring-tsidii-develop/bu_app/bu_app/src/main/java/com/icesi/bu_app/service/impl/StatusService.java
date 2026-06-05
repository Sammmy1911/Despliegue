package com.icesi.bu_app.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.icesi.bu_app.model.Status;
import com.icesi.bu_app.repository.IStatusRepository;
import com.icesi.bu_app.exception.ResourceNotFoundException;
import com.icesi.bu_app.service.IStatusService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatusService implements IStatusService {

    private final IStatusRepository statusRepository;

    @Override
    public List<Status> findAll() {
        return statusRepository.findAll();
    }

    @Override
    public Page<Status> findAll(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));
        return statusRepository.findAll(PageRequest.of(safePage, safeSize));
    }

    @Override
    public Status findById(Integer id) {
        return statusRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Status not found"));
    }

    @Override
    public Status save(Status status) {
        return statusRepository.save(status);
    }

    @Override
    public Status update(Integer id, Status status) {
        Status foundStatus = findById(id);
        foundStatus.setStatus(status.getStatus());
        return statusRepository.save(foundStatus);
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        statusRepository.deleteById(id);
    }
}