package com.example.demoonetomany.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demoonetomany.model.FileDB;
import com.example.demoonetomany.payload.response.MessageResponse;
import com.example.demoonetomany.payload.response.ResponseFile;
import com.example.demoonetomany.security.services.FileStorageService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class FileController {
  @Autowired
  private FileStorageService fileStorageService;

  @PostMapping("/upload")
  public ResponseEntity<MessageResponse> uploadFile(
    @RequestParam("file") MultipartFile file)
  {
    String message = "";
    try {
      fileStorageService.store(file);
      message = "Upload the file successfully: " + file.getOriginalFilename();
      return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
    } catch (Exception e) {
      message = "Could not upload the file: " + file.getOriginalFilename();
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
        .body(new MessageResponse(message));
    }
  }

  @GetMapping("/files")
  public ResponseEntity<List<ResponseFile>> getListFiles(){
    List<ResponseFile> files = fileStorageService.getAllFiles().map(dbFile -> {
      String fileDownloadUri = ServletUriComponentsBuilder
        .fromCurrentContextPath()
        .path("/api/files/")
        .path(dbFile.getId())
        .toUriString();

      return new ResponseFile(
        dbFile.getName(), fileDownloadUri, 
        dbFile.getType(), dbFile.getData().length);
    }).collect(Collectors.toList());

    return ResponseEntity.status(HttpStatus.OK).body(files);
  }

  @GetMapping("/files/{id}")
  public ResponseEntity<byte[]> getFile(@PathVariable String id){
    FileDB fileDB = fileStorageService.getFile(id);
    return ResponseEntity.ok()
      .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
      .body(fileDB.getData());
  }
}
