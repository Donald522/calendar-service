package calendar.configuration;

import calendar.dao.*;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@TestConfiguration
@Import(DbUnitConfig.class)
public class CalendarDaoTestConfig {

  @Bean
  public MeetingPersister singleMeetingPersister(JdbcTemplate jdbcTemplate) {
    return new SingleMeetingPersister(jdbcTemplate);
  }

  @Bean
  public MeetingPersister dailyMeetingPersister(JdbcTemplate jdbcTemplate) {
    return new DailyMeetingPersister(7, jdbcTemplate);
  }

  @Bean
  public MeetingPersister meetingPersisterDispatcher(MeetingPersister singleMeetingPersister,
                                                     MeetingPersister dailyMeetingPersister) {
    return new MeetingPersisterDispatcher(singleMeetingPersister, dailyMeetingPersister);
  }

  @Bean
  public CalendarDao calendarDao(JdbcTemplate jdbcTemplate, MeetingPersister meetingPersisterDispatcher) {
    return new H2CalendarDao(jdbcTemplate, meetingPersisterDispatcher);
  }
}
