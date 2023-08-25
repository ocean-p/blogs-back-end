package com.example.demoonetomany.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demoonetomany.model.Tag;
import com.example.demoonetomany.model.Tutorial;
import com.example.demoonetomany.repository.TagRepository;
import com.example.demoonetomany.repository.TutorialRepository;

@RestController
@RequestMapping("/api")
public class TagController {
  
  @Autowired
  private TutorialRepository tutorialRepository;
  
  @Autowired
  private TagRepository tagRepository;

  @GetMapping("/tags")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<List<Tag>> getAllTags(){
    List<Tag> tags = new ArrayList<Tag>();
    tagRepository.findAll().forEach(tags::add);

    if(tags.isEmpty()){
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    return new ResponseEntity<>(tags, HttpStatus.OK);
  }

  @GetMapping("/tutorials/{tutorialId}/tags")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<Tag>> getAllTagsByTutorialId(
    @PathVariable(value = "tutorialId") Long tutorialId
  ){
    if(!tutorialRepository.existsById(tutorialId)){
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    List<Tag> tags = tagRepository.findByTutorialsId(tutorialId);
    return new ResponseEntity<>(tags, HttpStatus.OK);
  }

  @GetMapping("/tags/{id}")
  public ResponseEntity<Tag> getTagsById(@PathVariable(value = "id") Long id) {
    Tag tag = tagRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Not found Tag with id = " + id));

    return new ResponseEntity<>(tag, HttpStatus.OK);
  }

  @GetMapping("/tags/{tagId}/tutorials")
  public ResponseEntity<List<Tutorial>> getAllTutorialsByTagId(
    @PathVariable(value = "tagId") Long tagId
  ){
    if(!tagRepository.existsById(tagId)){
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    List<Tutorial> tutorials = tutorialRepository.findByTagsId(tagId);
    return new ResponseEntity<>(tutorials, HttpStatus.OK);
  }

  @PostMapping("/tutorials/{tutorialId}/tags")
  public ResponseEntity<Tag> addTag(
    @PathVariable(value = "tutorialId") Long tutorialId,
    @RequestBody Tag tagRequest
  ){
    Tag tag = tutorialRepository.findById(tutorialId).map(tutorial -> {
      long tagId = tagRequest.getId();
      if(tagId != 0L){
        Tag _tag = tagRepository.findById(tagId).orElseThrow(null);
        tutorial.addTag(_tag);
        tutorialRepository.save(tutorial);
        return _tag;
      }

      tutorial.addTag(tagRequest);
      return tagRepository.save(tagRequest);
    }).orElseThrow(null);

    return new ResponseEntity<Tag>(tag, HttpStatus.OK);
  }

  @PutMapping("/tags/{id}")
  public ResponseEntity<Tag> updateTag(@PathVariable("id") long id, @RequestBody Tag tagRequest) {
    Tag tag = tagRepository.findById(id).orElseThrow(null);

    tag.setName(tagRequest.getName());

    return new ResponseEntity<>(tagRepository.save(tag), HttpStatus.OK);
  }

  @DeleteMapping("/tutorials/{tutorialId}/tags/{tagId}")
  public ResponseEntity<HttpStatus> deleteTagFromTutorial(
    @PathVariable(value = "tutorialId") Long tutorialId,
    @PathVariable(value = "tagId") Long tagId
  ){
    Tutorial tutorial = tutorialRepository.findById(tutorialId).orElseThrow(null);
    tutorial.removeTag(tagId);
    tutorialRepository.save(tutorial);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping("/tags/{id}")
  public ResponseEntity<HttpStatus> deleteTag(@PathVariable("id") long id) {
    tagRepository.deleteById(id);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
