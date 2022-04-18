package calendar.app.configuration;

import calendar.service.meeting.MeetingTransformer;
import calendar.service.model.Recurrence;
import com.google.common.collect.ImmutableMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CalendarConfig {

  @Bean
  public Map<Recurrence, MeetingTransformer> transformerMap(MeetingTransformer singleMeetingTransformer,
                                                            MeetingTransformer dailyMeetingTransformer,
                                                            MeetingTransformer weekdaysMeetingTransformer,
                                                            MeetingTransformer weeklyMeetingTransformer,
                                                            MeetingTransformer yearlyMeetingTransformer,
                                                            MeetingTransformer monthlyMeetingTransformer) {
    return ImmutableMap.<Recurrence, MeetingTransformer>builder()
        .put(Recurrence.NONE, singleMeetingTransformer)
        .put(Recurrence.DAILY, dailyMeetingTransformer)
        .put(Recurrence.WEEKDAYS, weekdaysMeetingTransformer)
        .put(Recurrence.WEEKLY, weeklyMeetingTransformer)
        .put(Recurrence.YEARLY, yearlyMeetingTransformer)
        .put(Recurrence.MONTHLY, monthlyMeetingTransformer)
        .build();
  }
}
