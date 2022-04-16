package calendar.api.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class ErrorMessage {

  @JsonProperty("error")
  String error;
}
