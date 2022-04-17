package calendar.dao

import calendar.configuration.CalendarDaoTestConfig
import calendar.service.model.Meeting
import calendar.service.model.Recurrence
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
  def "Should create single meeting with calendar events"() {
    given:
    def meeting = Meeting.builder()
        .title('Potato day')
        .organizer('Petr I')
        .location('SaintP')
        .fromTime(LocalDateTime.parse('1710-05-05 10:00', formatter))
        .toTime(LocalDateTime.parse('1710-05-05 20:00', formatter))
        .visibility(Visibility.PRIVATE)
        .recurrence(Recurrence.NONE)
        .participants(['Ekaterina I'])
        .build()

    when:
    def ids = calendarDao.createMeeting(meeting)

    then:
    ids.size() == 1
    def id = ids[0].id
    id > 0
    ids[0].subId == 1

    def res = db.rows("select * from meetings where id = ${id}")
    res.size() == 1
    res[0]['ID'] == id
    res[0]['SUB_ID'] == 1
    res[0]['MEETING_TITLE'] == meeting.title
    res[0]['ORGANIZER'] == meeting.organizer
    res[0]['LOCATION'] == meeting.location
    res[0]['VISIBILITY'] == meeting.visibility.name()
    res[0]['RECURRENCE'] == meeting.recurrence.name()
    res[0]['FROM_TIME'].toLocalDateTime() == meeting.fromTime
    res[0]['TO_TIME'].toLocalDateTime() == meeting.toTime

    def event1 = db.rows("select * from calendar where meeting_id = ${id} and user_email = 'Ekaterina I'")
    Long.parseLong(event1[0]['MEETING_ID'] as String) == id
    event1[0]['MEETING_SUB_ID'] == -1
    event1[0]['USER_EMAIL'] == 'Ekaterina I'
    event1[0]['RESPONSE'] == 'TENTATIVE'

    def event2 = db.rows("select * from calendar where meeting_id = ${id} and user_email = 'Petr I'")
    Long.parseLong(event2[0]['MEETING_ID'] as String) == id
    event2[0]['MEETING_SUB_ID'] == -1
    event2[0]['USER_EMAIL'] == 'Petr I'
    event2[0]['RESPONSE'] == 'TENTATIVE'
  }

  @DatabaseSetup(value = "classpath:database/clean_db.xml", connection = "dbUnitDatabaseConnection")
  def "Should create daily meeting with calendar events"() {
    given:
    def meeting = Meeting.builder()
        .title('Clean teeth')
        .organizer('Mother')
        .location('Bathroom')
        .fromTime(LocalDateTime.parse('2022-04-01 10:00', formatter))
        .toTime(LocalDateTime.parse('2022-04-01 10:05', formatter))
        .visibility(Visibility.PRIVATE)
        .recurrence(Recurrence.DAILY)
        .participants(['Father'])
        .build()

    when:
    def ids = calendarDao.createMeeting(meeting)

    then:
    ids.size() == 7
    def id = ids[0].id
    id > 0

    def res = db.rows("select * from meetings where id = ${id}")
    res.size() == 7
    res[0]['ID'] == id
    res[0]['SUB_ID'] == 1
    res[0]['MEETING_TITLE'] == meeting.title
    res[0]['ORGANIZER'] == meeting.organizer
    res[0]['LOCATION'] == meeting.location
    res[0]['VISIBILITY'] == meeting.visibility.name()
    res[0]['RECURRENCE'] == meeting.recurrence.name()
    res[0]['FROM_TIME'].toLocalDateTime() == meeting.fromTime
    res[0]['TO_TIME'].toLocalDateTime() == meeting.toTime

    res[6]['ID'] == id
    res[6]['SUB_ID'] == 7
    res[6]['MEETING_TITLE'] == meeting.title
    res[6]['ORGANIZER'] == meeting.organizer
    res[6]['LOCATION'] == meeting.location
    res[6]['VISIBILITY'] == meeting.visibility.name()
    res[6]['RECURRENCE'] == meeting.recurrence.name()
    res[6]['FROM_TIME'].toLocalDateTime() == LocalDateTime.parse('2022-04-07 10:00', formatter)
    res[6]['TO_TIME'].toLocalDateTime() == LocalDateTime.parse('2022-04-07 10:05', formatter)

    def event1 = db.rows("select * from calendar where meeting_id = ${id} and user_email = 'Mother'")
    event1.size() == 1
    Long.parseLong(event1[0]['MEETING_ID'] as String) == id
    event1[0]['MEETING_SUB_ID'] == -1
    event1[0]['USER_EMAIL'] == 'Mother'
    event1[0]['RESPONSE'] == 'TENTATIVE'

    def event2 = db.rows("select * from calendar where meeting_id = ${id} and user_email = 'Father'")
    event2.size() == 1
    Long.parseLong(event2[0]['MEETING_ID'] as String) == id
    event2[0]['MEETING_SUB_ID'] == -1
    event2[0]['USER_EMAIL'] == 'Father'
    event2[0]['RESPONSE'] == 'TENTATIVE'
  }

  @DatabaseSetup(value = "classpath:database/given_meeting.xml", connection = "dbUnitDatabaseConnection")
  def "Should return meeting details"() {
    given:
    def meetingId = 1
    def meetingSubId = 1

    when:
    def optional = calendarDao.getMeetingDetails(meetingId, meetingSubId)

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
    def meetingSubId = 1

    when:
    def res = calendarDao.isPermitted(user, meetingId, meetingSubId)

    then:
    res

    where:
    user << ['dart.vader@imp.com', 'luk.skywalker@reb.com']
  }

  @DatabaseSetup(value = "classpath:database/given_public_and_private_meetings.xml", connection = "dbUnitDatabaseConnection")
  def "Only participant is permitted to see private meeting"() {
    given:
    def meetingId = 1
    def meetingSubId = 1

    when:
    def res = calendarDao.isPermitted(user, meetingId, meetingSubId)

    then:
    res == expected

    where:
    user                    | expected
    'dart.vader@imp.com'    | true
    'luk.skywalker@reb.com' | true
    'han.solo@reb.com'      | false
  }
}
