package calendar.service;

import calendar.dao.UserCalendarProvider;
import calendar.service.model.MeetingSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class UserCalendarAdapterImpl implements UserCalendarAdapter {

  private final UserCalendarProvider personalCalendarProvider;
  private final UserCalendarProvider restrictedCalendarProvider;

  @Override
  public Collection<MeetingSummary> getPersonalCalendar(String user, LocalDateTime from, LocalDateTime to) {
    return personalCalendarProvider.getUserCalendar(user, from, to);
  }

  @Override
  public Collection<MeetingSummary> getRestrictedCalendar(String user, LocalDateTime from, LocalDateTime to) {
    return restrictedCalendarProvider.getUserCalendar(user, from, to);
  }
}
