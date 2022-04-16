package calendar.dao;

import calendar.service.model.Meeting;
import calendar.service.model.MeetingResponse;

import java.util.Optional;

public interface CalendarDao {

  long createMeeting(Meeting meeting);

  boolean isPermitted(String user, long meetingId);

  Optional<Meeting> getMeetingDetails(long meetingId);

  void respondToMeeting(MeetingResponse meetingResponse);
}
