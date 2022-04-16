package calendar.service;

import calendar.service.model.MeetingSummary;

import java.time.LocalDateTime;
import java.util.Collection;

public interface UserCalendarAdapter {

  Collection<MeetingSummary> getPersonalCalendar(String user, LocalDateTime from, LocalDateTime to);

  Collection<MeetingSummary> getRestrictedCalendar(String user, LocalDateTime from, LocalDateTime to);
}
