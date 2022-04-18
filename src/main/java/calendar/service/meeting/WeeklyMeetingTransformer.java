package calendar.service.meeting;

import calendar.service.model.Meeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Component
public class WeeklyMeetingTransformer implements MeetingTransformer {

  private final int recurrenceDuration;

  @Autowired
  public WeeklyMeetingTransformer(@Value("${calendar.recurrence.duration.events:180}") int recurrenceDuration) {
    this.recurrenceDuration = recurrenceDuration;
  }

  @Override
  public List<Meeting> transform(Meeting meeting) {
    LocalDateTime startFrom = meeting.getFromTime();
    LocalDateTime startTo = meeting.getToTime();

    return IntStream.range(0, recurrenceDuration)
        .mapToObj(i -> meeting
            .withSubId(i + 1)
            .withFromTime(startFrom.plusWeeks(i))
            .withToTime(startTo.plusWeeks(i)))
        .collect(toList());
  }
}
