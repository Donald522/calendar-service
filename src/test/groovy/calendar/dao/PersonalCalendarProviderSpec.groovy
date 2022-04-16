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
class PersonalCalendarProviderSpec extends Specification {

  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

  @Autowired
  private JdbcTemplate jdbcTemplate

  private PersonalCalendarProvider personalCalendarProvider

  def setup() {
    personalCalendarProvider = new PersonalCalendarProvider(jdbcTemplate)
  }

  @DatabaseSetup(value = "classpath:database/given_personal_calendar.xml", connection = "dbUnitDatabaseConnection")
  def "Should provide detailed calendar within given time interval"() {
    given:
    def user = 'luk.skywalker@reb.com'
    def from = LocalDateTime.parse('1983-05-25 09:00', formatter)
    def to = LocalDateTime.parse('1983-05-25 22:00', formatter)

    when:
    def res = personalCalendarProvider.getUserCalendar(user, from, to)

    then:
    res.size() == 1
    res[0].meetingId == 2
    res[0].title == 'Standup'
    res[0].organizer == 'dart.vader@imp.com'
    res[0].fromTime == LocalDateTime.parse('1983-05-25 12:00', formatter)
    res[0].toTime == LocalDateTime.parse('1983-05-25 12:30', formatter)
  }
}
