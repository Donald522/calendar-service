package calendar.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TimeSlotDto {

  @JsonProperty("from")
  String from;

  @JsonProperty("to")
  String to;
}
