package calendar.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MeetingResponseDto {

  long meetingId;
  long meetingSubId;
  String user;
  String response;

  @JsonCreator
  public MeetingResponseDto(@JsonProperty(value = "meetingId", required = true) long meetingId,
                            @JsonProperty(value = "meetingSubId", defaultValue = "-1") long meetingSubId,
                            @JsonProperty(value = "user", required = true) String user,
                            @JsonProperty(value = "response", required = true) String response) {
    this.meetingId = meetingId;
    this.meetingSubId = meetingSubId;
    this.user = user;
    this.response = response;
  }
}
