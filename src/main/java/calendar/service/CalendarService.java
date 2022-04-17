package calendar.service;

import calendar.service.model.*;

import java.time.LocalDateTime;
import java.util.Collection;

public interface CalendarService {

  Collection<MeetingId> createMeeting(Meeting meeting);

  Meeting getMeeting(String requestor, long meetingId, long meetingSubId);

  Collection<MeetingSummary> getCalendarForUser(String requestor, String user, LocalDateTime fromTime, LocalDateTime toTime);

  void respondToMeeting(MeetingResponse meetingResponse);

  TimeSlot suggestMeetingSlot(MeetingSlotRequest slotRequest);
}
