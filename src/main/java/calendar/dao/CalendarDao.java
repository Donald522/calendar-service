package calendar.dao;

import calendar.service.model.Meeting;
import calendar.service.model.MeetingSummary;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;

public interface CalendarDao {

  long createMeeting(Meeting meeting);

  // defaulting to the whole today date
  default Collection<MeetingSummary> getUserCalendar(String user) {
    return getUserCalendar(user,
        LocalDate.now().atTime(LocalTime.MIN),
        LocalDate.now().atTime(LocalTime.MAX));
  }

  Collection<MeetingSummary> getUserCalendar(String user, LocalDateTime from, LocalDateTime to);

  Meeting getMeetingDetails(long meetingId);

  void acceptMeeting(String user, long meetingId);

  void rejectMeeting(String user, long meetingId);
}
