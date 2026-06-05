package com.icesi.bu_app.controller.rest.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticatedUserResponse {
    private Integer code;
    private String name;
    private String email;
    private String role;
    private List<String> authorities;
    private Integer trainerCode;
}
