package calendar.configuration;

import calendar.dao.H2UserDao;
import calendar.dao.UserDao;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@TestConfiguration
@Import(DbUnitConfig.class)
public class UserDaoTestConfig {

  @Bean
  public UserDao userDao(JdbcTemplate jdbcTemplate) {
    return new H2UserDao(jdbcTemplate);
  }
}
