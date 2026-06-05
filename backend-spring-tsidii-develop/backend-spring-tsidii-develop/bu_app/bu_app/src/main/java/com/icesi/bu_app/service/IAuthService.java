package com.icesi.bu_app.service;

import org.apache.coyote.BadRequestException;
import com.icesi.bu_app.controller.rest.dto.LoginRequest;
import com.icesi.bu_app.controller.rest.dto.TokenResponse;

public interface IAuthService {
    TokenResponse login(LoginRequest loginRequest) throws BadRequestException;
}
