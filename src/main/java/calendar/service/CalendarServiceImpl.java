package calendar.service;

import calendar.dao.CalendarDao;
import calendar.service.exception.InternalServiceException;
import calendar.service.exception.NotFoundException;
import calendar.service.model.Meeting;
import calendar.service.model.MeetingResponse;
import calendar.service.model.MeetingSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

  private final CalendarDao calendarDao;

  @Override
  @Transactional
  public long createMeeting(Meeting meeting) {
    try {
      return calendarDao.createMeeting(meeting);
    } catch (Exception e) {
      log.error("Cannot create meeting {}", meeting.getTitle(), e);
      throw new InternalServiceException(String.format(
          "Cannot create meeting %s", meeting.getTitle()), e);
    }
  }

  @Override
  @Transactional
  public Meeting getMeeting(long meetingId) {
    try {
      return Optional.ofNullable(calendarDao.getMeetingDetails(meetingId))
          .orElseThrow(() ->
              new NotFoundException(String.format("Meeting with id = [%s] was not found", meetingId)));
    } catch (Exception e) {
      log.error("Cannot retrieve meeting with id = {}", meetingId, e);
      throw new InternalServiceException(String.format(
          "Cannot retrieve meeting with id = %s", meetingId), e);

    }
  }

  @Override
  @Transactional
  public Collection<MeetingSummary> getCalendarForUser(String user, LocalDateTime fromTime, LocalDateTime toTime) {
    try {
      return calendarDao.getUserCalendar(user, fromTime, toTime);
    } catch (Exception e) {
      log.error("Cannot retrieve calendar for user {}", user, e);
      throw new InternalServiceException(String.format(
          "Cannot retrieve calendar for user %s", user), e);
    }
  }

  @Override
  @Transactional
  public void respondToMeeting(MeetingResponse meetingResponse) {
    try {
      calendarDao.respondToMeeting(meetingResponse);
      // send notification to meeting organizer
    } catch (Exception e) {
      log.error("Cannot save response to meeting {}", meetingResponse, e);
      throw new InternalServiceException(String.format(
          "Cannot save response to meeting %s", meetingResponse), e);
    }
  }
}
