package calendar.dao

import calendar.configuration.DbUnitConfig
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Subject(PersonalCalendarProvider)
@ContextConfiguration(classes = [DbUnitConfig])
@TestExecutionListeners([TransactionDbUnitTestExecutionListener, DependencyInjectionTestExecutionListener])
class RestrictedCalendarProviderSpec extends Specification {

  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

  @Autowired
  private JdbcTemplate jdbcTemplate

  private RestrictedCalendarProvider restrictedCalendarProvider

  def setup() {
    restrictedCalendarProvider = new RestrictedCalendarProvider(jdbcTemplate)
  }

  @DatabaseSetup(value = "classpath:database/given_restricted_calendar.xml", connection = "dbUnitDatabaseConnection")
  def "Should provide restricted calendar within given time interval"() {
    given:
    def user = 'luk.skywalker@reb.com'
    def from = LocalDateTime.parse('1983-05-25 09:00', formatter)
    def to = LocalDateTime.parse('1983-05-25 22:00', formatter)

    when:
    def res = restrictedCalendarProvider.getUserCalendar(user, from, to)

    then:
    res.size() == 2

    res[0].meetingId == -1
    res[0].title == ''
    res[0].organizer == ''
    res[0].fromTime == LocalDateTime.parse('1983-05-25 12:00', formatter)
    res[0].toTime == LocalDateTime.parse('1983-05-25 12:30', formatter)

    res[1].meetingId == 3
    res[1].title == 'Celebration'
    res[1].organizer == 'ewok.king@mi.ru'
    res[1].fromTime == LocalDateTime.parse('1983-05-25 16:00', formatter)
    res[1].toTime == LocalDateTime.parse('1983-05-25 20:00', formatter)
  }
}
