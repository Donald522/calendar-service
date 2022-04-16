package calendar.service

import calendar.dao.CalendarDao
import calendar.dao.UserDao
import calendar.service.model.MeetingSlotRequest
import calendar.service.model.MeetingSummary
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Subject(CalendarService)
class CalendarServiceSlotSuggestionSpec extends Specification {

  @Shared
  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

  @Unroll("Should find free #requiredDuration minutes slot for one participant")
  def "Given one participant should find free slot in calendar"() {
    given:
    def user = 'test.user@g.ru'
    def mSummary1 = MeetingSummary.builder()
        .fromTime(LocalDateTime.parse('2022-04-16 11:00', formatter))
        .toTime(LocalDateTime.parse('2022-04-16 11:30', formatter))
        .build()
    def mSummary2 = MeetingSummary.builder()
        .fromTime(LocalDateTime.parse('2022-04-16 14:00', formatter))
        .toTime(LocalDateTime.parse('2022-04-16 15:30', formatter))
        .build()

    and:
    def minimalSlotMinutes = 15
    def userDao = Stub(UserDao)
    def calendarDao = Stub(CalendarDao) {
      getUserCalendar(user, _ as LocalDateTime, _ as LocalDateTime) >> [mSummary1, mSummary2]
    }
    def currentDateProvider = Stub(CurrentDateProvider) {
      getNow() >> LocalDateTime.parse('2022-04-16 10:00', formatter)
    }
    def service = new CalendarServiceImpl(minimalSlotMinutes, calendarDao, userDao, currentDateProvider)

    when:
    def slotRequest = MeetingSlotRequest.builder()
        .durationMin(requiredDuration)
        .participants([user])
        .build()
    def slot = service.suggestMeetingSlot(slotRequest)

    then:
    slot.from.isEqual(from)
    slot.to.isEqual(to)

    where:
    requiredDuration | from                                               | to
    30               | LocalDateTime.parse('2022-04-16 10:00', formatter) | LocalDateTime.parse('2022-04-16 10:30', formatter)
    60               | LocalDateTime.parse('2022-04-16 10:00', formatter) | LocalDateTime.parse('2022-04-16 11:00', formatter)
    90               | LocalDateTime.parse('2022-04-16 11:30', formatter) | LocalDateTime.parse('2022-04-16 13:00', formatter)
  }

  @Unroll("Should find free #requiredDuration minutes slot for multiple participants")
  def "Given multiple participants should find free slot in calendar"() {
    given: "First participant"
    def user1 = 'petr.perviy@gmail.ru'
    def mSummary1 = MeetingSummary.builder()
        .fromTime(LocalDateTime.parse('2022-04-16 10:00', formatter))
        .toTime(LocalDateTime.parse('2022-04-16 11:00', formatter))
        .build()
    def mSummary2 = MeetingSummary.builder()
        .fromTime(LocalDateTime.parse('2022-04-16 14:00', formatter))
        .toTime(LocalDateTime.parse('2022-04-16 16:30', formatter))
        .build()

    and: "Second participant"
    def user2 = 'nikolay.vtoroy@gmail.ru'
    def mSummary3 = MeetingSummary.builder()
        .fromTime(LocalDateTime.parse('2022-04-16 10:00', formatter))
        .toTime(LocalDateTime.parse('2022-04-16 11:30', formatter))
        .build()
    def mSummary4 = MeetingSummary.builder()
        .fromTime(LocalDateTime.parse('2022-04-16 12:00', formatter))
        .toTime(LocalDateTime.parse('2022-04-16 13:00', formatter))
        .build()
    def mSummary5 = MeetingSummary.builder()
        .fromTime(LocalDateTime.parse('2022-04-16 14:00', formatter))
        .toTime(LocalDateTime.parse('2022-04-16 16:00', formatter))
        .build()

    and: "Third participant"
    def user3 = 'alexander.tretiy@gmail.ru'
    def mSummary6 = MeetingSummary.builder()
        .fromTime(LocalDateTime.parse('2022-04-16 10:30', formatter))
        .toTime(LocalDateTime.parse('2022-04-16 11:30', formatter))
        .build()
    def mSummary7 = MeetingSummary.builder()
        .fromTime(LocalDateTime.parse('2022-04-16 18:00', formatter))
        .toTime(LocalDateTime.parse('2022-04-16 20:00', formatter))
        .build()

    and:
    def minimalSlotMinutes = 15
    def userDao = Stub(UserDao)
    def calendarDao = Stub(CalendarDao) {
      getUserCalendar(user1, _ as LocalDateTime, _ as LocalDateTime) >> [mSummary1, mSummary2]
      getUserCalendar(user2, _ as LocalDateTime, _ as LocalDateTime) >> [mSummary3, mSummary4, mSummary5]
      getUserCalendar(user3, _ as LocalDateTime, _ as LocalDateTime) >> [mSummary6, mSummary7]
    }
    def currentDateProvider = Stub(CurrentDateProvider) {
      getNow() >> LocalDateTime.parse('2022-04-16 10:00', formatter)
    }
    def service = new CalendarServiceImpl(minimalSlotMinutes, calendarDao, userDao, currentDateProvider)

    when:
    def slotRequest = MeetingSlotRequest.builder()
        .durationMin(requiredDuration)
        .participants([user1, user2, user3])
        .build()
    def slot = service.suggestMeetingSlot(slotRequest)

    then:
    slot.from.isEqual(from)
    slot.to.isEqual(to)

    where:
    requiredDuration | from                                               | to
    30               | LocalDateTime.parse('2022-04-16 11:30', formatter) | LocalDateTime.parse('2022-04-16 12:00', formatter)
    60               | LocalDateTime.parse('2022-04-16 13:00', formatter) | LocalDateTime.parse('2022-04-16 14:00', formatter)
    90               | LocalDateTime.parse('2022-04-16 16:30', formatter) | LocalDateTime.parse('2022-04-16 18:00', formatter)
  }

  @Unroll("Should find free #requiredDuration minutes slot tomorrow")
  def "Given one participant is all-day busy should find slot tomorrow"() {
    given: "First participant"
    def user1 = 'petr.perviy@gmail.ru'
    def mSummary1 = MeetingSummary.builder()
        .fromTime(LocalDateTime.parse('2022-04-16 10:00', formatter))
        .toTime(LocalDateTime.parse('2022-04-16 11:00', formatter))
        .build()
    def mSummary2 = MeetingSummary.builder()
        .fromTime(LocalDateTime.parse('2022-04-16 14:00', formatter))
        .toTime(LocalDateTime.parse('2022-04-16 16:30', formatter))
        .build()
    def mSummary3 = MeetingSummary.builder()
        .fromTime(LocalDateTime.parse('2022-04-17 12:30', formatter))
        .toTime(LocalDateTime.parse('2022-04-17 16:30', formatter))
        .build()

    and: "Second participant"
    def user2 = 'nikolay.vtoroy@gmail.ru'
    def mSummary4 = MeetingSummary.builder()
        .fromTime(LocalDateTime.parse('2022-04-16 10:00', formatter))
        .toTime(LocalDateTime.parse('2022-04-17 11:30', formatter))
        .build()

    and:
    def minimalSlotMinutes = 15
    def userDao = Stub(UserDao)
    def calendarDao = Stub(CalendarDao) {
      getUserCalendar(user1, _ as LocalDateTime, _ as LocalDateTime) >> [mSummary1, mSummary2, mSummary3]
      getUserCalendar(user2, _ as LocalDateTime, _ as LocalDateTime) >> [mSummary4]
    }
    def currentDateProvider = Stub(CurrentDateProvider) {
      getNow() >> LocalDateTime.parse('2022-04-16 10:00', formatter)
    }
    def service = new CalendarServiceImpl(minimalSlotMinutes, calendarDao, userDao, currentDateProvider)

    when:
    def slotRequest = MeetingSlotRequest.builder()
        .durationMin(requiredDuration)
        .participants([user1, user2])
        .build()
    def slot = service.suggestMeetingSlot(slotRequest)

    then:
    slot.from.isEqual(from)
    slot.to.isEqual(to)

    where:
    requiredDuration | from                                               | to
    30               | LocalDateTime.parse('2022-04-17 11:30', formatter) | LocalDateTime.parse('2022-04-17 12:00', formatter)
    60               | LocalDateTime.parse('2022-04-17 11:30', formatter) | LocalDateTime.parse('2022-04-17 12:30', formatter)
    90               | LocalDateTime.parse('2022-04-17 16:30', formatter) | LocalDateTime.parse('2022-04-17 18:00', formatter)
  }
}
