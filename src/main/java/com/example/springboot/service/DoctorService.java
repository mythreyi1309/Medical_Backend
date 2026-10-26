package com.example.springboot.service;

import java.util.List;

import com.example.springboot.entity.UserEntity;

public interface DoctorService {

    List<UserEntity> getAllDoctors();

    UserEntity addDoctor(UserEntity doctor);

    void deleteDoctor(int id);
   
}
