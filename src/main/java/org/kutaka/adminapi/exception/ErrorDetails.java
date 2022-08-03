package org.kutaka.adminapi.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ErrorDetails {

  private LocalDateTime timestamp;
  private HttpStatus status;
  private String message;
  private String details;
}
