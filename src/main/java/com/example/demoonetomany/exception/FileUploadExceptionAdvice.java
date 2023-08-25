package com.example.demoonetomany.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.demoonetomany.payload.response.MessageResponse;

@RestControllerAdvice
public class FileUploadExceptionAdvice extends ResponseEntityExceptionHandler {
  
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<MessageResponse> handleMaxSizeException(MaxUploadSizeExceededException exc){
    return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
      .body(new MessageResponse("File is larger than 5MB!"));
  }
}
