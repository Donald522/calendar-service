package calendar.service.meeting;

import calendar.service.model.Meeting;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class WeekdaysMeetingTransformer implements MeetingTransformer {

  private final int recurrenceDuration;

  @Autowired
  public WeekdaysMeetingTransformer(@Value("${calendar.recurrence.duration.events:180}") int recurrenceDuration) {
    this.recurrenceDuration = recurrenceDuration;
  }

  @Override
  public List<Meeting> transform(Meeting meeting) {
    LocalDateTime startFrom = meeting.getFromTime();
    LocalDateTime startTo = meeting.getToTime();

    ImmutableList.Builder<Meeting> meetings = ImmutableList.builder();

    int i = 0, j = 0;
    while (j < this.recurrenceDuration) {
      LocalDateTime nextDay = startFrom.plusDays(i);
      if (!(nextDay.getDayOfWeek() == DayOfWeek.SATURDAY || nextDay.getDayOfWeek() == DayOfWeek.SUNDAY)) {
        Meeting nextMeeting = meeting
            .withSubId(j + 1)
            .withFromTime(startFrom.plusDays(i))
            .withToTime(startTo.plusDays(i));
        meetings.add(nextMeeting);
        j++;
      }
      i++;
    }
    return meetings.build();
  }
}
