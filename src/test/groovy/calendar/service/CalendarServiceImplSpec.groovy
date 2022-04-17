package calendar.service

import calendar.dao.CalendarDao
import calendar.dao.UserDao
import calendar.service.exception.BadRequestException
import calendar.service.exception.NotFoundException
import calendar.service.model.Meeting
import calendar.service.model.User
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

  def "Should throw NotFoundException if calendar is requested for non-existing user"() {
    given:
    def requestor = 'user1'
    def user = 'user1'
    def from = LocalDateTime.parse('2022-04-17 10:00', formatter)
    def to = LocalDateTime.parse('2022-04-17 10:05', formatter)

    and:
    def userDao = Stub(UserDao) {
      findOne(user) >> Optional.empty()
    }
    def service = new CalendarServiceImpl(
        15, Stub(CalendarDao), userDao, Stub(UserCalendarAdapter), Stub(CurrentDateProvider))

    when:
    def res = service.getCalendarForUser(requestor, user, from, to)

    then:
    thrown(NotFoundException)
  }

  def "Build personal calendar for requestor"() {
    given:
    def requestor = 'user1'
    def user = 'user1'
    def from = LocalDateTime.parse('2022-04-17 10:00', formatter)
    def to = LocalDateTime.parse('2022-04-17 10:05', formatter)

    and:
    def userCalendarAdapter = Mock(UserCalendarAdapter)
    def userDao = Stub(UserDao) {
      findOne(user) >> Optional.of(User.builder().build())
    }
    def service = new CalendarServiceImpl(
        15, Stub(CalendarDao), userDao, userCalendarAdapter, Stub(CurrentDateProvider))

    when:
    def res = service.getCalendarForUser(requestor, user, from, to)

    then:
    interaction {
      1 * userCalendarAdapter.getPersonalCalendar(user, from, to)
    }
  }

  def "Build restricted calendar for user"() {
    given:
    def requestor = 'user1'
    def user = 'user2'
    def from = LocalDateTime.parse('2022-04-17 10:00', formatter)
    def to = LocalDateTime.parse('2022-04-17 10:05', formatter)

    and:
    def userCalendarAdapter = Mock(UserCalendarAdapter)
    def userDao = Stub(UserDao) {
      findOne(user) >> Optional.of(User.builder().build())
    }
    def service = new CalendarServiceImpl(
        15, Stub(CalendarDao), userDao, userCalendarAdapter, Stub(CurrentDateProvider))

    when:
    def res = service.getCalendarForUser(requestor, user, from, to)

    then:
    interaction {
      1 * userCalendarAdapter.getRestrictedCalendar(user, from, to)
    }
  }
}
