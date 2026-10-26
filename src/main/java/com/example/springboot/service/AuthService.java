package com.example.springboot.service;

import com.example.springboot.dto.LoginRequest;
import com.example.springboot.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}