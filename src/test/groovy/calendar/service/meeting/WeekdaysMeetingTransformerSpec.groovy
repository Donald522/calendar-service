package calendar.service.meeting

import calendar.service.model.Meeting
import calendar.service.model.Recurrence
import calendar.service.model.Visibility
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Subject(WeekdaysMeetingTransformer)
class WeekdaysMeetingTransformerSpec extends Specification {

  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

  def "Should create meetings on weekdays"() {
    given:
    def numberOfEvents = 3
    def meeting = Meeting.builder()
        .id(1)
        .title('Standup')
        .organizer('ScrumMaster')
        .location('Skype')
        .fromTime(LocalDateTime.parse('2022-04-21 10:00', formatter))
        .toTime(LocalDateTime.parse('2022-04-21 11:00', formatter))
        .visibility(Visibility.PUBLIC)
        .recurrence(Recurrence.WEEKDAYS)
        .build()

    def transformer = new WeekdaysMeetingTransformer(numberOfEvents)

    when:
    def meetings = transformer.transform(meeting)

    then:
    meetings.size() == 3

    meetings[0].id == 1
    meetings[0].subId == 1
    meetings[0].fromTime == LocalDateTime.parse('2022-04-21 10:00', formatter)
    meetings[0].toTime == LocalDateTime.parse('2022-04-21 11:00', formatter)

    meetings[1].id == 1
    meetings[1].subId == 2
    meetings[1].fromTime == LocalDateTime.parse('2022-04-22 10:00', formatter)
    meetings[1].toTime == LocalDateTime.parse('2022-04-22 11:00', formatter)

    meetings[2].id == 1
    meetings[2].subId == 3
    meetings[2].fromTime == LocalDateTime.parse('2022-04-25 10:00', formatter)
    meetings[2].toTime == LocalDateTime.parse('2022-04-25 11:00', formatter)
  }
}
