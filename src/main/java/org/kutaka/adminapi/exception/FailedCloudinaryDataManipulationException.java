package org.kutaka.adminapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FailedCloudinaryDataManipulationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public FailedCloudinaryDataManipulationException(String message) {
    super(message);
  }
}