package com.example.demoonetomany;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.example.demoonetomany.controller.TutorialController;
import com.example.demoonetomany.model.Tutorial;
import com.example.demoonetomany.repository.TutorialRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TutorialController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ControllerUnitTest {
  @MockBean
  private TutorialRepository tutorialRepository;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldCreateTutorial() throws Exception {
    Tutorial tutorial = new Tutorial("This is title", "This is desc", true);
    mockMvc.perform(post("/api/tutorials").contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(tutorial)))
      .andExpect(status().isCreated())
      .andDo(print());
  }

  @Test
  void shouldReturnTutorial() throws Exception{
    long id = 100L;
    Tutorial tutorial = new Tutorial(
      id, "Spring boot", "Description", true);
    
    when(tutorialRepository.findById(id)).thenReturn(Optional.of(tutorial));

    mockMvc.perform(get("/api/tutorials/{id}", id)).andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(id))
      .andExpect(jsonPath("$.title").value(tutorial.getTitle()))
      .andExpect(jsonPath("$.description").value(tutorial.getDescription()))
      .andExpect(jsonPath("$.published").value(tutorial.isPublished()))
      .andDo(print());
  }

  @Test
  void shouldReturnNotFoundTutorial() throws Exception{
    long id = 100L;
    when(tutorialRepository.findById(id)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/tutorials/{id}", id))
      .andExpect(status().isNotFound())
      .andDo(print());
  }

  @Test
  void shouldReturnListOfTutorials() throws Exception{
    List<Tutorial> tutorials = new ArrayList<>(
      Arrays.asList(
        new Tutorial(1, "tut1", "desc1", false),
        new Tutorial(2, "tut2", "desc2", false),
        new Tutorial(3, "tut3", "desc3", false)
      )
    );

    when(tutorialRepository.findAll()).thenReturn(tutorials);

    mockMvc.perform(get("/api/tutorials"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.size()").value(tutorials.size()))
      .andDo(print());
  }

  @Test
  void shouldReturnListOfTutorialsWithFilter() throws Exception{
    List<Tutorial> tutorials = new ArrayList<>(
      Arrays.asList(
        new Tutorial(1, "Java Core", "desc1", false),
        new Tutorial(2, "Java Spring Boot", "desc2", false),
        new Tutorial(3, "ReactJS", "desc3", false)
      )
    );

    String title = "Java";
    MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
    paramsMap.add("title", title);

    when(tutorialRepository.findByTitleContaining(title)).thenReturn(tutorials);
    mockMvc.perform(get("/api/tutorials").params(paramsMap))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.size()").value(tutorials.size()))
      .andDo(print());

    tutorials = Collections.emptyList();
    when(tutorialRepository.findByTitleContaining(title)).thenReturn(tutorials);
    mockMvc.perform(get("/api/tutorials").params(paramsMap))
      .andExpect(status().isNoContent())
      .andDo(print());
  }

  @Test
  void shouldReturnNoContentWhenFilter() throws Exception{
    String title = "CSM";
    MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
    paramsMap.add("title", title);

    List<Tutorial> tutorials = Collections.emptyList();
    when(tutorialRepository.findByTitleContaining(title)).thenReturn(tutorials);

    mockMvc.perform(get("/api/tutorials").params(paramsMap))
      .andExpect(status().isNoContent())
      .andDo(print());
  }

  @Test
  void shouldUpdateTutorial() throws Exception{
    long id = 1L;
    Tutorial tutorial = new Tutorial(
      id, "Spring Boot", "Description", true);
    Tutorial updated = new Tutorial(
      id, "updated", "updated", false);

    when(tutorialRepository.findById(id)).thenReturn(Optional.of(tutorial));
    when(tutorialRepository.save(any(Tutorial.class))).thenReturn(updated);

    mockMvc.perform(put("/api/tutorials/{id}", id)
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(updated)))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.title").value(updated.getTitle()))
    .andExpect(jsonPath("$.description").value(updated.getDescription()))
    .andExpect(jsonPath("$.published").value(updated.isPublished()))
    .andDo(print());
  }

  @Test
  void shouldDeleteTutorial() throws Exception{
    long id = 1L;

    doNothing().when(tutorialRepository).deleteById(id);
    mockMvc.perform(delete("/api/tutorials/{id}", id))
      .andExpect(status().isNoContent())
      .andDo(print());
  }

  @Test
  void shouldDeleteAllTutorials() throws Exception{
    doNothing().when(tutorialRepository).deleteAll();

    mockMvc.perform(delete("/api/tutorials"))
      .andExpect(status().isNoContent())
      .andDo(print());
  }
}
