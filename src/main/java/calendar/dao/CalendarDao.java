package calendar.dao;

import calendar.service.model.Meeting;
import calendar.service.model.MeetingResponse;
import calendar.service.model.MeetingSummary;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface CalendarDao {

  long createMeeting(Meeting meeting);

  Collection<MeetingSummary> getUserCalendar(String user, LocalDateTime from, LocalDateTime to);

  Optional<Meeting> getMeetingDetails(long meetingId);

  void respondToMeeting(MeetingResponse meetingResponse);
}
