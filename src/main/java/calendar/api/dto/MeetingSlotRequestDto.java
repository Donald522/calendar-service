package calendar.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.util.Collection;

@Value
@Builder
public class MeetingSlotRequestDto {

  long durationMin;
  Collection<String> participants;

  @JsonCreator
  public MeetingSlotRequestDto(@JsonProperty(value = "durationMin", required = true) long durationMin,
                               @JsonProperty(value = "participants", required = true) Collection<String> participants) {
    this.durationMin = durationMin;
    this.participants = participants;
  }
}
