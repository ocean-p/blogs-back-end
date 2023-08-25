package com.example.demoonetomany.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demoonetomany.model.FileDB;

public interface FileDBRepository extends JpaRepository<FileDB, String> {
  
}
