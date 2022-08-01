package org.kutaka.adminapi.common;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponse {

  private HttpStatus status;
  private Object body;
  private ArrayList<String> errors;

  public ApiResponse() {
    this.status = HttpStatus.OK;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public void setStatus(HttpStatus status) {
    this.status = status;
  }

  public Object getBody() {
    return body;
  }

  public void setBody(Object body) {
    this.body = body;
  }

  public ArrayList<String> getErrors() {
    return errors;
  }

  public void setErrors(ArrayList<String> errors) {
    this.errors = errors;
  }

  public ResponseEntity<Object> send() {
    // status is specified
    return ResponseEntity.status(this.status.value()).body(this.body);

    // // status is not specified
    // return ResponseEntity.status(status).body(this.body);
  }

}
