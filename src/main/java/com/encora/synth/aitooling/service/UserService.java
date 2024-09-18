package com.encora.synth.aitooling.service;

import com.encora.synth.aitooling.dto.*;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    UserCreateResponse create(UserCreateRequest request);

    UserGetResponse getByEmail(String email);

    UserLoginResponse login(UserLoginRequest request);

    UserRefreshResponse refreshToken(HttpServletRequest request);

    String getUserID();

    void logout(String jwtToken);
}
