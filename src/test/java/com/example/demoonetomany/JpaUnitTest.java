package com.example.demoonetomany;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.demoonetomany.model.Tutorial;
import com.example.demoonetomany.repository.TutorialRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
public class JpaUnitTest {
  @Autowired
  private TestEntityManager testEntityManager;

  @Autowired
  TutorialRepository tutorialRepository;

  @Test
  public void should_find_no_tutorials_if_repository_is_empty(){
    Iterable tutorials = tutorialRepository.findAll();
    assertThat(tutorials).isEmpty();
  }

  @Test
  public void should_store_a_tutorial(){
    Tutorial tutorial = tutorialRepository.save(new Tutorial("Tut title", "Tut desc", true));
    
    assertThat(tutorial).hasFieldOrPropertyWithValue("title", "Tut title");
    assertThat(tutorial).hasFieldOrPropertyWithValue("description", "Tut desc");
    assertThat(tutorial).hasFieldOrPropertyWithValue("published", true);
  }

  @Test
  public void should_find_all_tutorials(){
    Tutorial tut1 = new Tutorial("tut#1", "desc#1", true);
    testEntityManager.persist(tut1); 
    Tutorial tut2 = new Tutorial("tut#2", "desc#2", false);
    testEntityManager.persist(tut2); 
    Tutorial tut3 = new Tutorial("tut#3", "desc#3", true);
    testEntityManager.persist(tut3);

    Iterable tutorials = tutorialRepository.findAll();
    assertThat(tutorials).hasSize(3).contains(tut1, tut2, tut3);
  }

  @Test
  public void should_find_tutorial_by_id(){
    Tutorial tut1 = new Tutorial(
      "tut-1", "desc-1", true);
    testEntityManager.persist(tut1);

    Tutorial tut2 = new Tutorial(
      "tut-2", "desc-2", true);
    testEntityManager.persist(tut2);
  
    Tutorial foundTutorial = tutorialRepository.findById(tut2.getId()).get();
    assertThat(foundTutorial).isEqualTo(tut2);
  }

  @Test
  public void should_find_published_tutorials(){
    Tutorial tut1 = new Tutorial(
      "tut-1", "desc-1", true);
    testEntityManager.persist(tut1);

    Tutorial tut2 = new Tutorial(
      "tut-2", "desc-2", false);
    testEntityManager.persist(tut2);
  
    Iterable tutorials = tutorialRepository.findByPublished(true);
    assertThat(tutorials).hasSize(4).contains(tut1);
  }

  @Test
  public void should_find_tutorial_by_title(){
    Tutorial tut1 = new Tutorial(
      "Java Core", "desc-1", true);
    testEntityManager.persist(tut1);

    Tutorial tut2 = new Tutorial(
      "Java Spring Boot", "desc-2", true);
    testEntityManager.persist(tut2);
  
    Iterable tutorials = tutorialRepository.findByTitleContaining("Java");
    assertThat(tutorials).hasSize(2).contains(tut1, tut2);
  }

  @Test
  public void should_update_tutorial_by_id(){
    Tutorial tut1 = new Tutorial(
      "tut-1", "desc-1", true);
    testEntityManager.persist(tut1);

    Tutorial tut2 = new Tutorial(
      "tut-2", "desc-2", true);
    testEntityManager.persist(tut2);
  
    Tutorial updated = new Tutorial(
      "updated tut-2", "updated desc-2", false);

    Tutorial tut = tutorialRepository.findById(tut2.getId()).get();
    tut.setTitle(updated.getTitle());
    tut.setDescription(updated.getDescription());
    tut.setPublished(updated.isPublished());
    testEntityManager.persist(tut);

    Tutorial checkTut = tutorialRepository.findById(tut2.getId()).get();
    assertThat(checkTut.getId()).isEqualTo(tut2.getId());
    assertThat(checkTut.getTitle()).isEqualTo(updated.getTitle());
    assertThat(checkTut.getDescription()).isEqualTo(updated.getDescription());
    assertThat(checkTut.isPublished()).isEqualTo(updated.isPublished());
  }

  @Test
  public void should_delete_tutorial_by_id(){
    Tutorial tut1 = new Tutorial(
      "tut-1", "desc-1", true);
    testEntityManager.persist(tut1);

    Tutorial tut2 = new Tutorial(
      "tut-2", "desc-2", true);
    testEntityManager.persist(tut2);
  
    tutorialRepository.deleteById(tut1.getId());
    Iterable tutorials = tutorialRepository.findAll();
    assertThat(tutorials).hasSize(4).contains(tut2);
  }

  @Test
  public void should_delete_all_tutorials(){
    Tutorial tut1 = new Tutorial(
      "tut-1", "desc-1", true);
    testEntityManager.persist(tut1);

    Tutorial tut2 = new Tutorial(
      "tut-2", "desc-2", true);
    testEntityManager.persist(tut2);
  
    tutorialRepository.deleteAll();
    assertThat(tutorialRepository.findAll()).isEmpty();
  }
}
