package calendar.service.model;

import lombok.Builder;
import lombok.Value;

import java.util.Collection;

@Value
@Builder
public class MeetingSlotRequest {

  long durationMin;
  Collection<String> participants;
}
