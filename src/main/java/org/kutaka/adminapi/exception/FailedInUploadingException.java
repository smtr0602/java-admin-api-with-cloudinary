package org.kutaka.adminapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FailedInUploadingException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public FailedInUploadingException(String message) {
    super(message);
  }
}