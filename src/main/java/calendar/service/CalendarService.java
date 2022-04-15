package calendar.service;

import calendar.service.model.Meeting;
import calendar.service.model.MeetingResponse;
import calendar.service.model.MeetingSummary;

import java.time.LocalDateTime;
import java.util.Collection;

public interface CalendarService {

  long createMeeting(Meeting meeting);

  Meeting getMeeting(long meetingId);

  Collection<MeetingSummary> getCalendarForUser(String user, LocalDateTime fromTime, LocalDateTime toTime);

  void respondToMeeting(MeetingResponse meetingResponse);
}
