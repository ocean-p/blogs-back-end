package com.example.demoonetomany.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tutorials")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Tutorial {

  @Id
  @GeneratedValue(
    strategy = GenerationType.SEQUENCE, generator = "tutorial_generator")
  private long id;

  @Column(name = "title")
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "published")
  private boolean published;

  @ManyToMany(
    fetch = FetchType.LAZY,
    cascade = {CascadeType.PERSIST, CascadeType.MERGE}
  )
  @JoinTable(
    name = "tutorial_tags",
    joinColumns = {@JoinColumn(name = "tutorial_id")},
    inverseJoinColumns = {@JoinColumn(name = "tag_id")}
  )
  private Set<Tag> tags = new HashSet<>();

  public Tutorial(String title, String description, boolean published) {
    this.title = title;
    this.description = description;
    this.published = published;
  }
  
  public Tutorial(long id, String title, String description, boolean published) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.published = published;
  }

  public void addTag(Tag tag){
    this.tags.add(tag);
    tag.getTutorials().add(this);
  }

  public void removeTag(long tagId){
    Tag tag = this.tags.stream().filter(t -> t.getId() == tagId).findFirst().orElse(null);
    if(tag != null){
      this.tags.remove(tag);
      tag.getTutorials().remove(this);
    }
  }
}
