package calendar.configuration;

import calendar.dao.CalendarDao;
import calendar.dao.H2CalendarDao;
import calendar.service.meeting.DailyMeetingTransformer;
import calendar.service.meeting.MeetingTransformer;
import calendar.service.meeting.MeetingTransformerDispatcher;
import calendar.service.meeting.SingleMeetingTransformer;
import calendar.service.model.Recurrence;
import com.google.common.collect.ImmutableMap;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

@TestConfiguration
@Import(DbUnitConfig.class)
public class CalendarDaoTestConfig {

  @Bean
  public MeetingTransformer singleMeetingTransformer() {
    return new SingleMeetingTransformer();
  }

  @Bean
  public MeetingTransformer dailyMeetingTransformer() {
    return new DailyMeetingTransformer(7);
  }

  @Bean
  public Map<Recurrence, MeetingTransformer> transformerMap(MeetingTransformer singleMeetingTransformer,
                                                            MeetingTransformer dailyMeetingTransformer) {
    return ImmutableMap.<Recurrence, MeetingTransformer>builder()
        .put(Recurrence.NONE, singleMeetingTransformer)
        .put(Recurrence.DAILY, dailyMeetingTransformer)
        .build();
  }

  @Bean
  public MeetingTransformer meetingTransformerDispatcher(Map<Recurrence, MeetingTransformer> transformerMap) {
    return new MeetingTransformerDispatcher(transformerMap);
  }

  @Bean
  public CalendarDao calendarDao(JdbcTemplate jdbcTemplate, MeetingTransformer meetingTransformerDispatcher) {
    return new H2CalendarDao(jdbcTemplate, meetingTransformerDispatcher);
  }
}
