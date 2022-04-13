package calendar.app.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@PropertySource(value = {"database.properties"})
public class DaoConfig {

  @Bean
  public DataSource h2CalendarDataSource(@Value("${calendar.db.url}") String url,
                                         @Value("${calendar.db.driverClassName}") String driverClassName,
                                         @Value("${calendar.db.username}") String user,
                                         @Value("${calendar.db.password}") String password) {

    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(url);
    config.setUsername(user);
    config.setPassword(password);
    config.setDriverClassName(driverClassName);
    config.setAutoCommit(false);

    return new HikariDataSource(config);
  }

  @Bean
  public JdbcTemplate calendarJdbcTemplate(DataSource h2CalendarDataSource) {
    return new JdbcTemplate(h2CalendarDataSource);
  }

  @Bean
  public PlatformTransactionManager transactionManager(DataSource h2CalendarDataSource) {
    return new DataSourceTransactionManager(h2CalendarDataSource);
  }
}
