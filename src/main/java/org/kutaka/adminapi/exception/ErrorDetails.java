package org.kutaka.adminapi.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public class ErrorDetails {

  private LocalDateTime timestamp;
  private HttpStatus status;
  private String message;
  private String details;

  public ErrorDetails(LocalDateTime timestamp, HttpStatus status, String message, String details) {
    this.timestamp = timestamp;
    this.status = status;
    this.message = message;
    this.details = details;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public void setStatus(HttpStatus status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

}
