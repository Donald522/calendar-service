package calendar.dao;

import calendar.service.model.Meeting;
import calendar.service.model.MeetingSummary;

import java.time.LocalDateTime;
import java.util.Collection;

public interface CalendarDao {

  long createMeeting(Meeting meeting);

  Collection<MeetingSummary> getUserCalendar(String user, LocalDateTime from, LocalDateTime to);

  Meeting getMeetingDetails(long meetingId);

  void acceptMeeting(String user, long meetingId);

  void rejectMeeting(String user, long meetingId);
}
