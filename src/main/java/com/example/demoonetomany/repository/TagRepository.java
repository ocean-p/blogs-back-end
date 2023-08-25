package com.example.demoonetomany.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demoonetomany.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
  List<Tag> findByTutorialsId(Long tutorialId);
}
