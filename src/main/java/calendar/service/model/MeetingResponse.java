package calendar.service.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MeetingResponse {

  long meetingId;
  String user;
  Response response;
}
