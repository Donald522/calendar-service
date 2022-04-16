package calendar.configuration;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;

@TestConfiguration
@PropertySource(value = {"database.properties"})
public class DbUnitConfig {

  @Bean
  public DataSource dataSource(@Value("${calendar.db.url}") String url,
                               @Value("${calendar.db.driverClassName}") String driverClassName,
                               @Value("${calendar.db.username}") String user,
                               @Value("${calendar.db.password}") String password) {
    SingleConnectionDataSource dataSource = new SingleConnectionDataSource(
        url, user, password, true);
    dataSource.setAutoCommit(false);
    dataSource.setDriverClassName(driverClassName);
    return dataSource;
  }

  @Bean
  public JdbcTemplate jdbcTemplate(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  @Bean
  public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection(DataSource h2CalendarDataSource) {
    DatabaseConfigBean bean = new DatabaseConfigBean();
    bean.setDatatypeFactory(new H2DataTypeFactory());

    DatabaseDataSourceConnectionFactoryBean factoryBean = new DatabaseDataSourceConnectionFactoryBean();
    factoryBean.setDataSource(h2CalendarDataSource);
    factoryBean.setDatabaseConfig(bean);
    return factoryBean;
  }
}
