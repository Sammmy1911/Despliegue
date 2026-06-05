package com.icesi.bu_app.controller.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.icesi.bu_app.controller.rest.dto.TrainerAssignRequest;
import com.icesi.bu_app.controller.rest.dto.UserResponse;
import com.icesi.bu_app.model.User;
import com.icesi.bu_app.service.IUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rest/users")
@RequiredArgsConstructor
public class UserRestController {
    private final IUserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> listUsers(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<User> users = userService.findAll(page, size);

        List<UserResponse> content = users.getContent().stream().map(this::toResponse).collect(Collectors.toList());

        Page<UserResponse> resp = new PageImpl<>(content, users.getPageable(), users.getTotalElements());

        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/trainers")
    public ResponseEntity<List<UserResponse>> listTrainers() {
        List<User> trainers = userService.findTrainers();
        List<UserResponse> resp = trainers.stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}/trainer")
    public ResponseEntity<UserResponse> assignTrainer(@PathVariable Integer userId, @RequestBody TrainerAssignRequest req) {
        User user = userService.findById(userId);

        if (req.getTrainerId() == null) {
            user.setTrainer(null);
            User updated = userService.update(userId, user);
            return ResponseEntity.ok(toResponse(updated));
        }

        User trainer = userService.findById(req.getTrainerId());
        user.setTrainer(trainer);
        User updated = userService.update(userId, user);
        return ResponseEntity.ok(toResponse(updated));
    }

    private UserResponse toResponse(User u) {
        UserResponse resp = new UserResponse();
        resp.setCode(u.getCode());
        resp.setName(u.getName());
        resp.setEmail(u.getEmail());

        if (u.getRole() != null) {
            resp.setRole(new UserResponse.RoleInfo(u.getRole().getId(), u.getRole().getType()));
        }

        if (u.getTrainer() != null) {
            resp.setTrainer(new UserResponse.TrainerInfo(u.getTrainer().getCode(), u.getTrainer().getName()));
        }

        return resp;
    }
}
