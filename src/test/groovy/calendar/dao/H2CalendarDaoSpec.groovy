package calendar.dao

import calendar.configuration.CalendarDaoTestConfig
import calendar.service.model.Meeting
import calendar.service.model.Visibility
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import groovy.sql.Sql
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import javax.sql.DataSource
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Subject(H2CalendarDao)
@ContextConfiguration(classes = [CalendarDaoTestConfig])
@TestExecutionListeners([TransactionDbUnitTestExecutionListener, DependencyInjectionTestExecutionListener])
class H2CalendarDaoSpec extends Specification {

  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

  @Autowired
  private CalendarDao calendarDao

  @Autowired
  private DataSource dataSource

  private Sql db

  def setup() {
    db = Sql.newInstance(dataSource)
  }

  @DatabaseSetup(value = "classpath:database/clean_db.xml", connection = "dbUnitDatabaseConnection")
  def "Should create meeting with calendar events"() {
    given:
    def meeting = Meeting.builder()
        .title('Potato day')
        .organizer('Petr I')
        .location('SaintP')
        .fromTime(LocalDateTime.parse('1710-05-05 10:00', formatter))
        .toTime(LocalDateTime.parse('1710-05-05 20:00', formatter))
        .visibility(Visibility.PRIVATE)
        .participants(['Ekaterina I'])
        .build()

    when:
    def id = calendarDao.createMeeting(meeting)

    then:
    id > 0
    def res = db.rows("select * from meetings where id = ${id}")
    res[0]['ID'] == id
    res[0]['MEETING_TITLE'] == meeting.title
    res[0]['ORGANIZER'] == meeting.organizer
    res[0]['LOCATION'] == meeting.location
    res[0]['VISIBILITY'] == meeting.visibility.name()
    res[0]['FROM_TIME'].toLocalDateTime() == meeting.fromTime
    res[0]['TO_TIME'].toLocalDateTime() == meeting.toTime

    def event1 = db.rows("select * from calendar where meeting_id = ${id} and user_email = 'Ekaterina I'")
    Long.parseLong(event1[0]['MEETING_ID'] as String) == id
    event1[0]['USER_EMAIL'] == 'Ekaterina I'
    event1[0]['RESPONSE'] == 'TENTATIVE'

    def event2 = db.rows("select * from calendar where meeting_id = ${id} and user_email = 'Petr I'")
    Long.parseLong(event2[0]['MEETING_ID'] as String) == id
    event2[0]['USER_EMAIL'] == 'Petr I'
    event2[0]['RESPONSE'] == 'TENTATIVE'
  }

  @DatabaseSetup(value = "classpath:database/given_meeting.xml", connection = "dbUnitDatabaseConnection")
  def "Should return meeting details"() {
    given:
    def meetingId = 1

    when:
    def optional = calendarDao.getMeetingDetails(meetingId)

    then:
    optional.isPresent()
    def meeting = optional.get()
    meeting.title == 'Standup'
    meeting.organizer == 'dart.vader@imp.com'
    meeting.location == 'Endor'
    meeting.fromTime == LocalDateTime.parse("1983-05-25 12:00", formatter)
    meeting.toTime == LocalDateTime.parse("1983-05-25 12:30", formatter)
    meeting.visibility == Visibility.PRIVATE
    meeting.participants.size() == 2
    meeting.participants.contains('dart.vader@imp.com')
    meeting.participants.contains('luk.skywalker@reb.com')
  }

  @Unroll("User #user is permitted to see public meeting")
  @DatabaseSetup(value = "classpath:database/given_public_and_private_meetings.xml", connection = "dbUnitDatabaseConnection")
  def "Any user is permitted to see public meeting"() {
    given:
    def meetingId = 2

    when:
    def res = calendarDao.isPermitted(user, meetingId)

    then:
    res

    where:
    user << ['dart.vader@imp.com', 'luk.skywalker@reb.com']
  }

  @DatabaseSetup(value = "classpath:database/given_public_and_private_meetings.xml", connection = "dbUnitDatabaseConnection")
  def "Only participant is permitted to see private meeting"() {
    given:
    def meetingId = 1

    when:
    def res = calendarDao.isPermitted(user, meetingId)

    then:
    res == expected

    where:
    user                    | expected
    'dart.vader@imp.com'    | true
    'luk.skywalker@reb.com' | true
    'han.solo@reb.com'      | false
  }
}
