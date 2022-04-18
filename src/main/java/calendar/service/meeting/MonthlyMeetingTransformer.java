package calendar.service.meeting;

import calendar.service.model.Meeting;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.dayOfWeekInMonth;

@Component
public class MonthlyMeetingTransformer implements MeetingTransformer {

  private final int recurrenceDuration;

  @Autowired
  public MonthlyMeetingTransformer(@Value("${calendar.recurrence.duration.events:180}") int recurrenceDuration) {
    this.recurrenceDuration = recurrenceDuration;
  }

  @Override
  public List<Meeting> transform(Meeting meeting) {
    DayOfWeek dayOfWeek = meeting.getFromTime().getDayOfWeek();
    int ordinal = ((meeting.getFromTime().getDayOfMonth() - 1) / 7) + 1;

    ImmutableList.Builder<Meeting> meetings = ImmutableList.builder();

    for (int i = 0; i < this.recurrenceDuration; i++) {
      Meeting nextMeeting = meeting
          .withSubId(i + 1)
          .withFromTime(meeting.getFromTime()
              .plusMonths(i).with(dayOfWeekInMonth(ordinal, dayOfWeek)))
          .withToTime(meeting.getToTime()
              .plusMonths(i).with(dayOfWeekInMonth(ordinal, dayOfWeek)));
      meetings.add(nextMeeting);
    }

    return meetings.build();
  }
}
