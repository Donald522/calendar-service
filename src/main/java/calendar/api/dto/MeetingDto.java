package calendar.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.util.Collection;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeetingDto {

  String title;
  String organizer;
  String location;
  String fromTime;
  String toTime;
  String message;
  String visibility;
  String recurrence;
  Collection<String> participants;

  @JsonCreator
  public MeetingDto(@JsonProperty(value = "title") String title,
                    @JsonProperty(value = "organizer", required = true) String organizer,
                    @JsonProperty(value = "fromTime", required = true) String fromTime,
                    @JsonProperty(value = "toTime", required = true) String toTime,
                    @JsonProperty(value = "location") String location,
                    @JsonProperty(value = "message") String message,
                    @JsonProperty(value = "visibility", defaultValue = "PUBLIC") String visibility,
                    @JsonProperty(value = "recurrence", defaultValue = "NONE") String recurrence,
                    @JsonProperty(value = "participants") Collection<String> participants) {
    this.title = title;
    this.organizer = organizer;
    this.fromTime = fromTime;
    this.toTime = toTime;
    this.location = location;
    this.message = message;
    this.visibility = visibility;
    this.recurrence = recurrence;
    this.participants = participants;
  }
}
