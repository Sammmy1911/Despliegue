package com.icesi.bu_app.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.icesi.bu_app.model.Alert;
import com.icesi.bu_app.model.User;
import com.icesi.bu_app.repository.IAlertRepository;
import com.icesi.bu_app.repository.IUserRepository;
import com.icesi.bu_app.service.IAlertService;
import com.icesi.bu_app.exception.BadRequestException;
import com.icesi.bu_app.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertService implements IAlertService {

    private final IAlertRepository alertRepository;
    private final IUserRepository userRepository;

    @Override
    public List<Alert> findAll() {
        return alertRepository.findAll();
    }

    @Override
    public Page<Alert> findAll(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));
        return alertRepository.findAll(PageRequest.of(safePage, safeSize));
    }

    @Override
    public Alert findById(Integer id) {
        return alertRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Alert not found"));
    }

    @Override
    public Alert save(Alert alert) {
        alert.setTrainer(resolveRequiredUser(alert.getTrainer(), "trainer"));
        alert.setTrainee(resolveRequiredUser(alert.getTrainee(), "trainee"));
        return alertRepository.save(alert);
    }

    @Override
    public Alert update(Integer id, Alert alert) {
        Alert foundAlert = findById(id);
        foundAlert.setSendDate(alert.getSendDate());
        foundAlert.setMessage(alert.getMessage());
        foundAlert.setTrainer(resolveRequiredUser(alert.getTrainer(), "trainer"));
        foundAlert.setTrainee(resolveRequiredUser(alert.getTrainee(), "trainee"));
        return alertRepository.save(foundAlert);
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        alertRepository.deleteById(id);
    }

    private User resolveRequiredUser(User user, String role) {
        if (user == null || user.getCode() == null) {
            throw new BadRequestException("Alert must have a " + role);
        }
        return userRepository.findById(user.getCode())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}