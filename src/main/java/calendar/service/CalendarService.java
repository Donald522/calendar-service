package calendar.service;

import calendar.service.model.Meeting;

public interface CalendarService {

  long createMeeting(Meeting meeting);

  Meeting getMeeting(long meetingId);
}
