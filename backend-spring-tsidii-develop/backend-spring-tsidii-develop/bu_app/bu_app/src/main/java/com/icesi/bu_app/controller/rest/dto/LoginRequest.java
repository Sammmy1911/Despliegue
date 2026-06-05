package com.icesi.bu_app.controller.rest.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
