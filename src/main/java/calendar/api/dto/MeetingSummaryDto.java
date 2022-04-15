package calendar.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MeetingSummaryDto {

  long meetingId;
  String title;
  String organizer;
  String fromTime;
  String toTime;

  @JsonCreator
  public MeetingSummaryDto(@JsonProperty(value = "meetingId", required = true) long meetingId,
                           @JsonProperty(value = "title") String title,
                           @JsonProperty(value = "organizer") String organizer,
                           @JsonProperty(value = "fromTime", required = true) String fromTime,
                           @JsonProperty(value = "toTime", required = true) String toTime) {
    this.meetingId = meetingId;
    this.title = title;
    this.organizer = organizer;
    this.fromTime = fromTime;
    this.toTime = toTime;
  }
}
