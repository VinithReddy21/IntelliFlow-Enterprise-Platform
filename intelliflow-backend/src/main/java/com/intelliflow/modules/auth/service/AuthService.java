package com.intelliflow.modules.auth.service;

import com.intelliflow.modules.auth.dto.JwtResponseDto;
import com.intelliflow.modules.auth.dto.LoginRequestDto;
import com.intelliflow.modules.auth.dto.RefreshTokenRequestDto;

public interface AuthService {

    JwtResponseDto login(LoginRequestDto loginRequest);

    JwtResponseDto refresh(RefreshTokenRequestDto refreshRequest);

    void logout(String accessToken, String refreshToken);
}
