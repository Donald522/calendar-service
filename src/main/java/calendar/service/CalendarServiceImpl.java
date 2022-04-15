package calendar.service;

import calendar.dao.CalendarDao;
import calendar.service.exception.InternalServiceException;
import calendar.service.model.Meeting;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
      return calendarDao.getMeetingDetails(meetingId);
    } catch (Exception e) {
      log.error("Cannot retrieve meeting with id = {}", meetingId, e);
      throw new InternalServiceException(String.format(
          "Cannot retrieve meeting with id = %s", meetingId), e);

    }
  }
}
