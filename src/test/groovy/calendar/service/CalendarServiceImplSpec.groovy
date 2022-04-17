package calendar.service

import calendar.dao.CalendarDao
import calendar.dao.UserDao
import calendar.service.exception.BadRequestException
import calendar.service.model.Meeting
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Subject(CalendarServiceImpl)
class CalendarServiceImplSpec extends Specification {

  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

  def "Should not create meeting with duration less than given threshold"() {
    given:
    def meeting = Meeting.builder()
        .fromTime(LocalDateTime.parse('2022-04-17 10:00', formatter))
        .toTime(LocalDateTime.parse('2022-04-17 10:05', formatter))
    .build()

    def service = new CalendarServiceImpl(
        15, Stub(CalendarDao), Stub(UserDao), Stub(UserCalendarAdapter), Stub(CurrentDateProvider))

    when:
    service.createMeeting(meeting)

    then:
    thrown(BadRequestException)
  }


}
