package com.moveinsync.billingreportservice.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class MisErrorHttpResponse {

  private String error;
  private String message;
  private long timestamp;
  private String path;
  private String cause;

}
