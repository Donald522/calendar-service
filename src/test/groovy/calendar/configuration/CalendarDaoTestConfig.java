package calendar.configuration;

import calendar.dao.CalendarDao;
import calendar.dao.H2CalendarDao;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@TestConfiguration
@Import(DbUnitConfig.class)
public class CalendarDaoTestConfig {

  @Bean
  public CalendarDao calendarDao(JdbcTemplate jdbcTemplate) {
    return new H2CalendarDao(jdbcTemplate);
  }
}
