package calendar.service.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class TimeSlot {

  LocalDateTime from;
  LocalDateTime to;
}
