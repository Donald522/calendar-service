package calendar.service.meeting;

import calendar.service.model.Meeting;
import com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SingleMeetingTransformer implements MeetingTransformer {

  @Override
  public List<Meeting> transform(Meeting meeting) {
    return ImmutableList.of(meeting.withSubId(1));
  }
}
