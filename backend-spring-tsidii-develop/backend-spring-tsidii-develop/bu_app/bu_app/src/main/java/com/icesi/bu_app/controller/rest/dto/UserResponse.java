package com.icesi.bu_app.controller.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Integer code;
    private String name;
    private String email;
    private RoleInfo role;
    private TrainerInfo trainer;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleInfo {
        private Integer id;
        private String type;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrainerInfo {
        private Integer id;
        private String name;
    }
}
