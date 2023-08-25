package com.example.demoonetomany.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {

  @Id
  @GeneratedValue(
    strategy = GenerationType.SEQUENCE, generator = "comment_generator")
  private long id;

  @Column(name = "content")
  private String content;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "tutorial_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Tutorial tutorial;
}
