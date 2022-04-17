package calendar.service.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class MeetingSummary {

  Long meetingId;
  Long meetingSubId;
  String title;
  String organizer;
  LocalDateTime fromTime;
  LocalDateTime toTime;
}
