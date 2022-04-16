package calendar.service;

import calendar.service.model.*;

import java.time.LocalDateTime;
import java.util.Collection;

public interface CalendarService {

  long createMeeting(Meeting meeting);

  Meeting getMeeting(String requestor, long meetingId);

  Collection<MeetingSummary> getCalendarForUser(String requestor, String user, LocalDateTime fromTime, LocalDateTime toTime);

  void respondToMeeting(MeetingResponse meetingResponse);

  TimeSlot suggestMeetingSlot(MeetingSlotRequest slotRequest);
}
