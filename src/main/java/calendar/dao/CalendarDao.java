package calendar.dao;

import calendar.service.model.Meeting;
import calendar.service.model.MeetingId;
import calendar.service.model.MeetingResponse;

import java.util.Collection;
import java.util.Optional;

public interface CalendarDao {

  Collection<MeetingId> createMeeting(Meeting meeting);

  boolean isPermitted(String user, long meetingId, long meetingSubId);

  Optional<Meeting> getMeetingDetails(long meetingId, long meetingSubId);

  void respondToMeeting(MeetingResponse meetingResponse);
}
