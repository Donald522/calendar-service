package calendar.service.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.LocalDateTime;
import java.util.Collection;

@With
@Value
@Builder
public class Meeting {

  long id;
  long subId;
  String title;
  String organizer;
  String location;
  LocalDateTime fromTime;
  LocalDateTime toTime;
  String message;
  Visibility visibility;
  Recurrence recurrence;
  Collection<String> participants;
}
