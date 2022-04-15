package calendar.service.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class MeetingSummary {

  long meetingId;
  String title;
  LocalDateTime fromTime;
  LocalDateTime toTime;
}
